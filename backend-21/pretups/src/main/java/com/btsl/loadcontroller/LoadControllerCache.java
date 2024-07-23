package com.btsl.loadcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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

/*
 * LoadControllerCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for updating the memory values at the startup
 */

public class LoadControllerCache  implements Runnable {

    private static Hashtable<String,InstanceLoadVO> _instanceLoadHash = new Hashtable<String,InstanceLoadVO>();
    private static Hashtable<String,NetworkLoadVO> _networkLoadHash = new Hashtable<String,NetworkLoadVO>();
    private static Hashtable<String,InterfaceLoadVO> _interfaceLoadHash  = new Hashtable<String,InterfaceLoadVO>();
    private static Hashtable<String,TransactionLoadVO> _transactionLoadHash = new Hashtable<String,TransactionLoadVO>();
    private static Hashtable _networkServiceLoadHash = new Hashtable();

    private static Hashtable _networkServiceHourlyLoadHash = new Hashtable();
    private static HashMap _allTransactionLoadListMap = new HashMap();
    private static Log _log = LogFactory.getLog(LoadControllerCache.class.getName());
    private static String _instanceID = null;
    private static String _loadType = null;

    private static HashMap _instanceLoadObjectMap = new HashMap();
    private static HashMap _networkLoadObjectMap = new HashMap();
    
    private static HashMap _interfaceLoadObjectMap = new HashMap();
    private static HashMap _transactionLoadObjectMap = new HashMap();

    // HashTable to load instance details with network
    private static Hashtable _instanceLoadForNetworkHash = new Hashtable();
	private static long _transactionIDCounter=0L;
	private static long _bufferTxnIDCounter=0L;
	private static long MAX_COUNTER=99999999L;
	
    private String instanceId = null; 
    private boolean fileRead = true;
    
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyinstanceLoadHash = "instanceLoadHash";
    private static final String hkeyinsLoadForNetworkHash = "instanceLoadForNetworkHash";
    private static final String hKeyInterfaceLoadHash = "interfaceLoadHash";
    private static final String hKeyNetworkLoadHash = "networkLoadHash";
    private static final String hKeyTXNLoadHash = "transactionLoadHash";
    private static final String hKeyNetServiceLoadHash = "networkServiceLoadHash";
    private static final String hKeyNetServiceHourHash = "networkServiceHourlyLoadHash";

    private static  int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,HashMap<String,InstanceLoadVO>>  insLoadhashMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)  
	    .refreshAfterWrite(keyTimer, TimeUnit.MILLISECONDS)   
	    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
	    .build(new CacheLoader<String,HashMap<String,InstanceLoadVO>>(){
			@Override
			public HashMap<String,InstanceLoadVO> load(String key) throws Exception {
				return  getinstanceLoadHashFromRedis(key);
			}
	     });
    
    private static LoadingCache<String,InstanceLoadVO>  insLoadhashForNetworkMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)  
	    .refreshAfterWrite(keyTimer, TimeUnit.MILLISECONDS)   
	    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
	    .build(new CacheLoader<String,InstanceLoadVO>(){
			@Override
			public InstanceLoadVO load(String key) throws Exception {
				return  getinstanceLoadForNetworkHashFromRedis(key);
			}
	     });
    
    private static LoadingCache<String,HashMap<String,InterfaceLoadVO>>  intfLoadhashMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)  
	    .refreshAfterWrite(keyTimer, TimeUnit.MILLISECONDS)   
	    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
	    .build(new CacheLoader<String,HashMap<String,InterfaceLoadVO>>(){
			@Override
			public HashMap<String,InterfaceLoadVO> load(String key) throws Exception {
				return  getInterfaceLoadHashFromRedis(key);
			}
	     });
    
    private static LoadingCache<String,HashMap<String,TransactionLoadVO>>  trfLoadhashMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)  
    	    .refreshAfterWrite(keyTimer, TimeUnit.MILLISECONDS)   
    	    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
    	    .build(new CacheLoader<String,HashMap<String,TransactionLoadVO>>(){
    			@Override
    			public HashMap<String,TransactionLoadVO> load(String key) throws Exception {
    				return  getTrfLoadHashFromRedis(key);
    			}
    	     });
    
    private static LoadingCache<String,HashMap<String,NetworkLoadVO>>  networkLoadhashMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)  
	    .refreshAfterWrite(keyTimer, TimeUnit.MILLISECONDS)   
	    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
	    .build(new CacheLoader<String,HashMap<String,NetworkLoadVO>>(){
			@Override
			public HashMap<String,NetworkLoadVO> load(String key) throws Exception {
				return  getNetworkLoadHashFromRedis(key);
			}
	     });

    private static LoadingCache<String,HashMap<String,NetworkServiceLoadVO>>  networkServiceLoadhashMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)  
    	    .refreshAfterWrite(keyTimer, TimeUnit.MILLISECONDS)   
    	    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
    	    .build(new CacheLoader<String,HashMap<String,NetworkServiceLoadVO>>(){
    			@Override
    			public HashMap<String,NetworkServiceLoadVO> load(String key) throws Exception {
    				return  getNetworkServiceLoadHashFromRedis(key);
    			}
    	     });
        
    private static LoadingCache<String,HashMap<String,NetworkServiceHourlyLoadVO>>  netServiceHourlyLoadhashMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)  
    	    .refreshAfterWrite(keyTimer, TimeUnit.MILLISECONDS)   
    	    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
    	    .build(new CacheLoader<String,HashMap<String,NetworkServiceHourlyLoadVO>>(){
    			@Override
    			public HashMap<String,NetworkServiceHourlyLoadVO> load(String key) throws Exception {
    				return  getNetworkServiceHourlyLoadHashFromRedis(key);
    			}
    	     });
            
    public LoadControllerCache(String pInstanceId, boolean pRileRead ){
    	this.instanceId = pInstanceId;
    	this.fileRead = pRileRead;
    }
    public void run() {
        try {
            Thread.sleep(50);
            refreshInstanceLoad(instanceId, fileRead);
            refreshNetworkLoad(fileRead);
            refreshInterfaceLoad(fileRead);
            refreshTransactionLoad(fileRead);
            refreshNetworkServiceCounters(instanceId, fileRead);
            refreshNetworkServiceHourlyCounters(instanceId);
        } catch (Exception e) {
        	 _log.error("LookupsCache init() Exception ", e);
        }
    }
    public static InstanceLoadVO getInstanceLoadForNetworkHash(String p_networkCode) {
	 InstanceLoadVO instanceLoadVO = null;
     String methodName = "getInstanceLoadForNetworkHash";
     	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
         	try{
         		instanceLoadVO = insLoadhashForNetworkMap.get(p_networkCode);
         	}catch(ExecutionException je){
     			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
     	        _log.errorTrace(methodName, je);
                 EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "ExecutionException :" + je.getMessage());
     		}catch(InvalidCacheLoadException  ex){
     			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
     	        _log.errorTrace(methodName, ex);
                 EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
     		}catch (Exception e){
     			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
     	        _log.errorTrace(methodName, e);
                 EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
     		}
     	}
     	else{
     		instanceLoadVO = (InstanceLoadVO) _instanceLoadForNetworkHash.get(p_networkCode);
     	}
    return instanceLoadVO;
    }

    public static String getInstanceID() {
        return _instanceID;
    }

    public static void setInstanceID(String instanceID) {
        _instanceID = instanceID;
    }

    public static String getLoadType() {
        return _loadType;
    }

    public static void setLoadType(String loadType) {
        _loadType = loadType;
    }

    public static Hashtable getInstanceLoadHash() {
    	String methodName = "getInstanceLoadHash";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		HashMap<String,InstanceLoadVO>insMap = insLoadhashMap.get(hKeyinstanceLoadHash);
				if(insMap != null){
					updateLocalInstanceLoadHash( _instanceLoadHash,insMap);
				}
        	}catch(ExecutionException je){
    			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    	        _log.errorTrace(methodName, je);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "ExecutionException :" + je.getMessage());
    		}catch(InvalidCacheLoadException  ex){
    			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
    		}catch (Exception e){
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    	        _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
    		}
    	}
        return _instanceLoadHash;
    }

    public static void setInstanceLoadHash(Hashtable instanceLoadHash) {
        _instanceLoadHash = instanceLoadHash;
    }

    public static Hashtable getNetworkLoadHash() {
    	String methodName = "getNetworkLoadHash";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		HashMap<String,NetworkLoadVO>insMap = networkLoadhashMap.get(hKeyNetworkLoadHash);
				if(insMap != null){
					updateLocalNetworkLoadHash( _networkLoadHash,insMap);
				}
        	}catch(ExecutionException je){
    			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    	        _log.errorTrace(methodName, je);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "ExecutionException :" + je.getMessage());
    		}catch(InvalidCacheLoadException  ex){
    			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
    		}catch (Exception e){
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    	        _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
    		}
    	}
        return _networkLoadHash;
    }

    public static void setNetworkLoadHash(Hashtable networkLoadHash) {
        _networkLoadHash = networkLoadHash;
    }

    public static void refreshInstanceLoad(String p_instanceID, boolean p_fileRead) // Pass
    // from
    // config
    // servlets
    {
        final String methodName = "refreshInstanceLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: p_instanceID: ");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_fileRead: ");
        	loggerValue.append(p_fileRead);
            _log.debug(methodName,loggerValue);
        }
        refreshInstanceLoad(p_instanceID);
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("INSTANCE_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("File exists file absolute path: ");
                    	loggerValue.append(file.getAbsolutePath());
                        _log.debug(methodName,loggerValue);
                    }
                    Hashtable instanceHashRead = read(file);
                    if (!(instanceHashRead == null || instanceHashRead.isEmpty())) {
                        modifyInstanceLoadVO(_instanceLoadHash, instanceHashRead);
                    }
                } else {
            		loggerValue.setLength(0);
                	loggerValue.append("File does not exist file absolute path: " );
                	loggerValue.append(file.getAbsolutePath());
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName,loggerValue);
                    }
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }
    }

    public static void refreshNetworkLoad(boolean p_fileRead) {
        final String methodName = "refreshNetworkLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_fileRead:" + p_fileRead);
        }
        refreshNetworkLoad();
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("NETWORK_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File exists file absolute path: " + file.getAbsolutePath());
                    }
                    Hashtable networkHashRead = read(file);
                    if (!(networkHashRead == null || networkHashRead.isEmpty())) {
                    	 modifyNetworkLoadVO(_networkLoadHash, networkHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File does not exist file absolute path: " + file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
            	_log.error(methodName, "Exception=" + e);
            	_log.errorTrace(methodName, e);
               
            }

        }
    }

    public static void refreshInterfaceLoad(boolean p_fileRead) {
        final String methodName = "refreshInterfaceLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_fileRead: " + p_fileRead);
        }
        refreshInterfaceLoad();
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("INTERFACE_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File exists file absolute path: " + file.getAbsolutePath());
                    }
                    Hashtable interfaceHashRead = read(file);
                    if (!(interfaceHashRead == null || interfaceHashRead.isEmpty())) {  
                    	modifyInterfaceLoadVO(_interfaceLoadHash, interfaceHashRead);                   				
                    } 
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File does not exist file absolute path: " + file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }
    }

    public static void refreshTransactionLoad(boolean p_fileRead) {
        final String methodName = "refreshTransactionLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_fileRead: " + p_fileRead);
        }
        refreshTransactionLoad();
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("TRANSACTION_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File exists file absolute path: " + file.getAbsolutePath());
                    }
                    Hashtable transactionHashRead = read(file);
                    if (!(transactionHashRead == null || transactionHashRead.isEmpty())) {
                    	modifyTransactionLoadVO(_transactionLoadHash, transactionHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File does not exist file absolute path: " + file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }
    }

    public static void refreshNetworkServiceCounters(String p_instanceID, boolean p_fileRead) // Pass
    // from
    // config
    // servlets
    {
        final String methodName = "refreshNetworkServiceCounters";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered p_instanceID: ");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_fileRead: ");
        	loggerValue.append(p_fileRead);
            _log.debug(methodName,loggerValue);
        }
        refreshNetworkServiceCounters(p_instanceID);
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("NETWORK_SERVICE_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File exists file absolute path: " + file.getAbsolutePath());
                    }
                    Hashtable networkServiceLoadHashRead = read(file);
                    if (!(networkServiceLoadHashRead == null || networkServiceLoadHashRead.isEmpty())) {
                    	modifyNetworkServiceLoadVO(getNetworkServiceLoadHash(), networkServiceLoadHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("File does not exist file absolute path: ");
                    	loggerValue.append(file.getAbsolutePath());
                        _log.debug(methodName,loggerValue);
                    }
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }

    }

    public static void refreshNetworkServiceHourlyCounters(String p_instanceID, boolean p_fileRead) // Pass
    // from
    // config
    // servlets
    {
        final String methodName = "refreshNetworkServiceHourlyCounters";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered p_instanceID: ");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_fileRead: ");
        	loggerValue.append(p_fileRead);
            _log.debug(methodName,loggerValue);
        }
        refreshNetworkServiceHourlyCounters(p_instanceID);
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("NETWORK_SERVICE_HOURLY_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File exists file absolute path: " + file.getAbsolutePath());
                    }
                    Hashtable networkServiceLoadHoulryHashRead = read(file);
                    if (!(networkServiceLoadHoulryHashRead == null || networkServiceLoadHoulryHashRead.isEmpty())) {
         				 modifyNetworkServiceHourlyLoadVO(getNetworkServiceHourlyLoadHash(), networkServiceLoadHoulryHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("File does not exist file absolute path: ");
                    	loggerValue.append(file.getAbsolutePath());
                        _log.debug(methodName,loggerValue);
                    }
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }

    }

	public static void refreshInstanceLoad(String p_instanceID) // Pass from
    // config
    // servlets
    {
		final String methodName = "refreshInstanceLoad";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
		    _log.debug(methodName, "Entered: ");
		}
		Connection con = null;
		try {
		    _instanceID = p_instanceID;
		    if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
		    	loggerValue.append(" Before loading:");
		    	loggerValue.append(getInstanceLoadHash().size());
		        _log.debug(methodName,loggerValue);
		    }
		    LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
		    con = OracleUtil.getSingleConnection();
		
		    _instanceLoadHash = loadControllerDAO.loadInstanceLoadDetails(p_instanceID);
		   _instanceLoadForNetworkHash = loadControllerDAO.loadInstanceLoadDetailsForNetwork(con);
		
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            Jedis jedis = null;
			 try {
			     RedisActivityLog.log("LoadControllerCache->refreshInstanceLoad->Start");
  				 jedis = RedisConnectionPool.getPoolInstance().getResource();
  				 Pipeline pipeline = jedis.pipelined();
				 Set<String> keys = _instanceLoadHash.keySet();
			 	 Iterator<String> itr = keys.iterator();
				 while (itr.hasNext()) { 
			       String str = itr.next();
			       pipeline.hset(hKeyinstanceLoadHash, str, gson.toJson(_instanceLoadHash.get(str)));
				 }
 				 InstanceLoadVO instanceLoadVO = (InstanceLoadVO) _instanceLoadHash.get(_instanceID);
   	             if (instanceLoadVO != null) {
   	                _loadType = instanceLoadVO.getLoadType();
   	             }
	             Set<String> nKeys = _instanceLoadForNetworkHash.keySet();
  				 Iterator<String> itr1 = nKeys.iterator();
  				 while (itr1.hasNext()) { 
  			       String str = itr1.next();
			       pipeline.hset(hkeyinsLoadForNetworkHash, str, gson.toJson(_instanceLoadForNetworkHash.get(str)));
  				 }
				 pipeline.sync();
    		     RedisActivityLog.log("LoadControllerCache->refreshInstanceLoad->End");
			 }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
			 }catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 }catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
			 }finally{
				if(jedis != null)
					jedis.close();
			 }
      	   	} 
            else{
            	InstanceLoadVO instanceLoadVO = (InstanceLoadVO) getInstanceLoadHash().get(_instanceID);
 	            if (instanceLoadVO != null) {
 	                _loadType = instanceLoadVO.getLoadType();
 	            }
            }
            
            if (!getInstanceLoadObjectMap().containsKey(_instanceID)) {
                _instanceLoadObjectMap.put(_instanceID, new InstanceLoadController());
            }
             if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(getInstanceLoadHash().size());
                _log.debug(methodName,loggerValue);
            }
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshInstanceLoad]", "", "", "", "Exception:" + e.getMessage());
        }finally{
			try {
				if (con != null)
					con.close();
			} 
			
			catch (SQLException e)
			{
        		loggerValue.setLength(0);
            	loggerValue.append("SQLException:e=");
            	loggerValue.append(e);
        		_log.error(methodName,loggerValue);
        		_log.errorTrace(methodName, e);
        	}
        }
    }

    public static void refreshNetworkLoad() {
        final String methodName = "refreshNetworkLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: ");
        }
        Jedis jedis = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Before loading:" + _networkLoadHash.size());
            }
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
	        _networkLoadHash = loadControllerDAO.loadNetworkLoadDetails(_instanceID, _loadType, true);
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		RedisActivityLog.log("LoadControllerCache->refreshNetworkLoad->Start");
        	    jedis = RedisConnectionPool.getPoolInstance().getResource();
        	    Pipeline pipeline = jedis.pipelined();
        		Set<String> keys = _networkLoadHash.keySet();
  				Iterator<String> itr = keys.iterator();
  				 while (itr.hasNext()) { 
  			       String str = itr.next();
  			       pipeline.hset(hKeyNetworkLoadHash,str, gson.toJson(_networkLoadHash.get(str)));
  				 }
  				 pipeline.sync();
         		RedisActivityLog.log("LoadControllerCache->refreshNetworkLoad->End");
            }
      		    
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " After loading:" + _networkLoadHash.size());
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		     _log.error(methodName, "Exception " + e.getMessage());
		     _log.errorTrace(methodName, e);
		     EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
		 }finally{
			if(jedis != null)
				jedis.close();
		 } 
 }

    public static void refreshInterfaceLoad() {
        final String methodName = "refreshInterfaceLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: ");
        }
        Jedis jedis = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Before loading:" + _interfaceLoadHash.size());
            }
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _interfaceLoadHash = loadControllerDAO.loadInterfaceLoadDetails(_instanceID, _loadType, true);

            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
         		RedisActivityLog.log("LoadControllerCache->refreshInterfaceLoad->Start");
     			 jedis = RedisConnectionPool.getPoolInstance().getResource();			 
     			 Pipeline pipeline = jedis.pipelined();
 	             Set<String> keys = _interfaceLoadHash.keySet();
				 Iterator<String> itr = keys.iterator();
				 while (itr.hasNext()) { 
			       String str = itr.next();
			       pipeline.hset(hKeyInterfaceLoadHash,str, gson.toJson(_interfaceLoadHash.get(str)));
				 }
				 pipeline.sync();
          		RedisActivityLog.log("LoadControllerCache->refreshInterfaceLoad->End");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " After loading:" + _interfaceLoadHash.size());
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
	            _log.error(methodName, "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	        }finally{
			if(jedis != null)
				jedis.close();
		 } 
    }

    public static void refreshTransactionLoad() {
        final String methodName = "refreshTransactionLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: ");
        }
        Jedis jedis = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Before loading:" + _transactionLoadHash.size());
            }
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _transactionLoadHash = loadControllerDAO.loadTransactionLoadDetails(_instanceID, _loadType, true);
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
         		RedisActivityLog.log("LoadControllerCache->refreshTransactionLoad->Start");
         	    jedis = RedisConnectionPool.getPoolInstance().getResource();
         	    Pipeline pipeline = jedis.pipelined();
         		Set<String> keys = _transactionLoadHash.keySet();
				 Iterator<String> itr = keys.iterator();
				 while (itr.hasNext()) { 
			       String str = itr.next();
			       pipeline.hset(hKeyTXNLoadHash, str, gson.toJson(_transactionLoadHash.get(str)));
				 }
				pipeline.sync();
				RedisActivityLog.log("LoadControllerCache->refreshTransactionLoad->End");
          }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " After loading:" + _transactionLoadHash.size());
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshTransactionLoad]", "", "", "", "Exception:" + e.getMessage());
	     }finally{
			if(jedis != null)
				jedis.close();
		 }
    }

    public static Hashtable getInterfaceLoadHash() {
    	String methodName = "getInterfaceLoadHash";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		HashMap<String,InterfaceLoadVO>insMap = intfLoadhashMap.get(hKeyInterfaceLoadHash);
				if(insMap != null){
					updateInterfaceLoadHash( _interfaceLoadHash,insMap);
				}
        	}catch(ExecutionException je){
    			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    	        _log.errorTrace(methodName, je);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "ExecutionException :" + je.getMessage());
    		}catch(InvalidCacheLoadException  ex){
    			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
    		}catch (Exception e){
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    	        _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
    		}
    	}
    	return _interfaceLoadHash;
    }

    public static void setInterfaceLoadHash(Hashtable interfaceLoadHash) {
        _interfaceLoadHash = interfaceLoadHash;
    }

    public static Hashtable getTransactionLoadHash() {
    	String methodName = "getTransactionLoadHash";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		HashMap<String,TransactionLoadVO>insMap = trfLoadhashMap.get(hKeyTXNLoadHash);
				if(insMap != null){
					updateTransactionLoadHash( _transactionLoadHash,insMap);
				}
        	}catch(ExecutionException je){
    			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    	        _log.errorTrace(methodName, je);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "ExecutionException :" + je.getMessage());
    		}catch(InvalidCacheLoadException  ex){
    			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
    		}catch (Exception e){
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    	        _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
    		}
    	}
    	return _transactionLoadHash;
    }

    public static void setTransactionLoadHash(Hashtable transactionLoadHash) {
        _transactionLoadHash = transactionLoadHash;
    }

    public static HashMap getAllTransactionLoadListMap() {
        return _allTransactionLoadListMap;
    }

    public static void setAllTransactionLoadListMap(HashMap<String,MiniTransVO> allTransactionLoadListMap) {
        _allTransactionLoadListMap = allTransactionLoadListMap;
    }

    public static HashMap getInstanceLoadObjectMap() {
        return _instanceLoadObjectMap;
    }

    public static void setInstanceLoadObjectMap(HashMap<String,InstanceLoadController> instanceLoadObjectMap) {
        _instanceLoadObjectMap = instanceLoadObjectMap;
    }

    public static HashMap getInterfaceLoadObjectMap() {
        return _interfaceLoadObjectMap;
    }

    public static void setInterfaceLoadObjectMap(HashMap<String,InterfaceLoadController> interfaceLoadObjectMap) {
        _interfaceLoadObjectMap = interfaceLoadObjectMap;
    }

    public static HashMap getNetworkLoadObjectMap() {
        return _networkLoadObjectMap;
    }

    public static void setNetworkLoadObjectMap(HashMap<String,NetworkLoadController> networkLoadObjectMap) {
        _networkLoadObjectMap = networkLoadObjectMap;
    }

    public static HashMap getTransactionLoadObjectMap() {
        return _transactionLoadObjectMap;
    }

    public static void setTransactionLoadObjectMap(HashMap<String,TransactionLoadController> transactionLoadObjectMap) {
        _transactionLoadObjectMap = transactionLoadObjectMap;
    }

    /**
     * Method to initialize Instance load
     * 
     * @param p_instanceID
     */
    public static void initializeInstanceLoad(String p_instanceID) {
        final String methodName = "initializeInstanceLoad";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Entered: with p_instanceID=");
        	loggerValue.append(p_instanceID);
            _log.debug(methodName,loggerValue);
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t1 = new Timestamp(date.getTime());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,InstanceLoadVO> tempHash = loadControllerDAO.loadInstanceLoadDetails(p_instanceID);
            InstanceLoadVO instanceLoadVO = null;
            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
        				loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    if (getInstanceLoadHash() != null && getInstanceLoadHash().containsKey(key)) {
                        instanceLoadVO = (InstanceLoadVO) getInstanceLoadHash().get(key);
                        instanceLoadVO.setCurrentTransactionLoad(0);
                        instanceLoadVO.setFirstRequestCount(0);
                        instanceLoadVO.setFirstRequestTime(0);
                        instanceLoadVO.setLastInitializationTime(date);
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, " last initialize time is changed to :" + instanceLoadVO.getLastInitializationTime());
                        }
                        instanceLoadVO.setLastReceievedTime(t1);
                        instanceLoadVO.setLastRefusedTime(null);
                        instanceLoadVO.setNoOfRequestSameSec(0);
                        instanceLoadVO.setReceiverCurrentTransactionLoad(0);
                        instanceLoadVO.setRecievedCount(0);
                        instanceLoadVO.setRequestCount(0);
                        instanceLoadVO.setRequestTimeoutSec(0);
                        instanceLoadVO.setTotalRefusedCount(0);
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                        	RedisActivityLog.log("LoadControllerCache->initializeInstanceLoad->Start");
              	   		    jedis = RedisConnectionPool.getPoolInstance().getResource();
              				jedis.hset(hKeyinstanceLoadHash,key,gson.toJson(instanceLoadVO));
                      		RedisActivityLog.log("LoadControllerCache->initializeInstanceLoad->End");
                        }
                    } else {
                        if (_log.isDebugEnabled()) {
            				loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
            				loggerValue.append(" in _instanceLoadHash=");
                        	loggerValue.append(_instanceLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }
                        if (_instanceLoadHash == null) {
                            _instanceLoadHash = new Hashtable();
                        }
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                        	RedisActivityLog.log("LoadControllerCache->initializeInstanceLoad->Start");
              	   		    jedis = RedisConnectionPool.getPoolInstance().getResource();
              				jedis.hset(hKeyinstanceLoadHash,key,gson.toJson(instanceLoadVO));
                      		RedisActivityLog.log("LoadControllerCache->initializeInstanceLoad->End");
                        }
                        
                        _instanceLoadHash.put(key, tempHash.get(key));
                        if (_instanceLoadObjectMap == null) {
                            _instanceLoadObjectMap = new HashMap();
                        }
                        if (!_instanceLoadObjectMap.containsKey(_instanceID)) {
                            _instanceLoadObjectMap.put(_instanceID, new InstanceLoadController());
                        }
                        }
                }
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeInstanceLoad]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeInstanceLoad]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e){
			loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeInstanceLoad]", "", "", "", "Exception:" + e.getMessage());	
		}finally{
			if(jedis != null)
				jedis.close();
		}
    }

    /**
     * Method to initialize Network load
     */
    public static void initializeNetworkLoad() {
        final String methodName = "initializeNetworkLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t1 = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,NetworkLoadVO> tempHash = loadControllerDAO.loadNetworkLoadDetails(_instanceID, _loadType, false);
            NetworkLoadVO networkLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getNetworkLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }

                    if (getNetworkLoadHash() != null && getNetworkLoadHash().containsKey(key)) {
                        networkLoadVO = (NetworkLoadVO) getNetworkLoadHash().get(key);
                        networkLoadVO.setCurrentTransactionLoad(0);
                        networkLoadVO.setFirstRequestCount(0);
                        networkLoadVO.setFirstRequestTime(0);
                        networkLoadVO.setLastInitializationTime(date);
                        networkLoadVO.setLastReceievedTime(t1);
                        networkLoadVO.setLastRefusedTime(null);
                        networkLoadVO.setNoOfRequestSameSec(0);
                        networkLoadVO.setReceiverCurrentTransactionLoad(0);
                        networkLoadVO.setRecievedCount(0);
                        networkLoadVO.setRequestCount(0);
                        networkLoadVO.setRequestTimeoutSec(0);
                        networkLoadVO.setTotalRefusedCount(0);
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                     		RedisActivityLog.log("LoadControllerCache->initializeNetworkLoad->Start");
                     	    jedis = RedisConnectionPool.getPoolInstance().getResource();
                     		jedis.hset(hKeyNetworkLoadHash,key,gson.toJson(networkLoadVO));
                     		RedisActivityLog.log("LoadControllerCache->initializeNetworkLoad->End");
                        }
                    } else {
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _networkLoadHash=");
                        	loggerValue.append(_networkLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }

                   	 if (_networkLoadHash == null) {
                            _networkLoadHash = new Hashtable();
                        }
                   	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                 		RedisActivityLog.log("LoadControllerCache->initializeNetworkLoad->Start");
                 	    jedis = RedisConnectionPool.getPoolInstance().getResource();
                 		jedis.hset(hKeyNetworkLoadHash,key,gson.toJson(tempHash.get(key)));
                 		RedisActivityLog.log("LoadControllerCache->initializeNetworkLoad->End");
                      }
                      _networkLoadHash.put(key, tempHash.get(key));
                       if (hashMap == null) {
                            hashMap = new HashMap();
                      }
                      if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new NetworkLoadController());
                            LoadControllerCache.setNetworkLoadObjectMap(hashMap);
                       }
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_networkLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName +"]", "", "", "", "Exception:" + e.getMessage());
	        }finally{
			if(jedis != null)
				jedis.close();
		 } 
    }

    /**
     * Method to initialize Interface load
     */
    public static void initializeInterfaceLoad() {
        final String methodName = "initializeInterfaceLoad";
		StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,InterfaceLoadVO> tempHash = loadControllerDAO.loadInterfaceLoadDetails(_instanceID, _loadType, false);
            InterfaceLoadVO interfaceLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getInterfaceLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    if (getInterfaceLoadHash() != null && getInterfaceLoadHash().containsKey(key)) {
                        interfaceLoadVO = (InterfaceLoadVO) getInterfaceLoadHash().get(key);
                        interfaceLoadVO.setCurrentTransactionLoad(0);
                        interfaceLoadVO.setFirstRequestCount(0);
                        interfaceLoadVO.setFirstRequestTime(0);
                        interfaceLoadVO.setLastInitializationTime(date);
                        interfaceLoadVO.setLastReceievedTime(t);
                        interfaceLoadVO.setLastRefusedTime(null);
                        interfaceLoadVO.setNoOfRequestSameSec(0);
                        interfaceLoadVO.setReceiverCurrentTransactionLoad(0);
                        interfaceLoadVO.setRecievedCount(0);
                        interfaceLoadVO.setRequestCount(0);
                        interfaceLoadVO.setRequestTimeoutSec(0);
                        interfaceLoadVO.setTotalRefusedCount(0);
                        interfaceLoadVO.setCurrentQueueSize(0);
                        interfaceLoadVO.setLastQueueAdditionTime(t);
                        interfaceLoadVO.setLastQueueAdditionTime(t);
                        interfaceLoadVO.setLastQueueCaseCheckTime(0);
                        interfaceLoadVO.setLastReceievedTime(t);
                        interfaceLoadVO.setLastTxnProcessStartTime(t);
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                     	RedisActivityLog.log("LoadControllerCache->initializeInterfaceLoad->Start1");
               	   		    jedis = RedisConnectionPool.getPoolInstance().getResource();
               				jedis.hset(hKeyInterfaceLoadHash,key,gson.toJson(interfaceLoadVO));
                  		RedisActivityLog.log("LoadControllerCache->initializeInterfaceLoad->End1");
               		}
                    } else {

                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _interfaceLoadHash=");
                        	loggerValue.append(_interfaceLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName, loggerValue);
                        }
                        

                  	   if (_interfaceLoadHash == null) {
                             _interfaceLoadHash = new Hashtable();
                         }
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	                        RedisActivityLog.log("LoadControllerCache->initializeInterfaceLoad->Start2");                       
	               	   		jedis = RedisConnectionPool.getPoolInstance().getResource();
	                 		jedis.hset(hKeyInterfaceLoadHash,key,gson.toJson(tempHash.get(key)));
	                      	RedisActivityLog.log("LoadControllerCache->initializeInterfaceLoad->End2");
                       }
                        _interfaceLoadHash.put(key, tempHash.get(key));
                        if (hashMap == null) {
                            hashMap = new HashMap();
                        }
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new InterfaceLoadController());
                            LoadControllerCache.setInterfaceLoadObjectMap(hashMap);
                        }
                    
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_interfaceLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
		        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception ");
			loggerValue.append(e.getMessage());
		    _log.error(methodName,loggerValue);
		    _log.errorTrace(methodName, e);
		    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeInterfaceLoad]", "", "", "", "Exception:" + e.getMessage());
		}finally{
			if(jedis != null)
				jedis.close();
		 } 
}

    /**
     * Method to initialize Transaction load
     */
    public static void initializeTransactionLoad() {
        final String methodName = "initializeTransactionLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,TransactionLoadVO> tempHash = loadControllerDAO.loadTransactionLoadDetails(_instanceID, _loadType, false);
            TransactionLoadVO transactionLoadVO = null;
            TransactionLoadVO alternateTransactionLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
               
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getTransactionLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    if (getTransactionLoadHash() != null && getTransactionLoadHash().containsKey(key)) {
                        transactionLoadVO = (TransactionLoadVO) getTransactionLoadHash().get(key);
                        transactionLoadVO.setCurrentTransactionLoad(0);
                        transactionLoadVO.setFirstRequestCount(0);
                        transactionLoadVO.setFirstRequestTime(0);
                        transactionLoadVO.setLastInitializationTime(date);
                        transactionLoadVO.setLastReceievedTime(t);
                        transactionLoadVO.setLastRefusedTime(null);
                        transactionLoadVO.setNoOfRequestSameSec(0);
                        transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                        transactionLoadVO.setRecievedCount(0);
                        transactionLoadVO.setRequestCount(0);
                        transactionLoadVO.setRequestTimeoutSec(0);
                        transactionLoadVO.setTotalRefusedCount(0);
                        transactionLoadVO.setLastReceievedTime(t);
                        transactionLoadVO.setLastTxnProcessStartTime(t);
                        transactionLoadVO.setCurrentRecieverTopupCount(0);
                        transactionLoadVO.setCurrentSenderTopupCount(0);
                        transactionLoadVO.setCurrentRecieverValidationCount(0);
                        transactionLoadVO.setCurrentSenderValidationCount(0);
                        transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                        transactionLoadVO.setTotalInternalFailCount(0);
                        transactionLoadVO.setTotalRecieverTopupCount(0);
                        transactionLoadVO.setTotalRecieverTopupFailCount(0);
                        transactionLoadVO.setTotalRecieverValFailCount(0);
                        transactionLoadVO.setTotalRecieverValidationCount(0);
                        transactionLoadVO.setTotalSenderTopupCount(0);
                        transactionLoadVO.setTotalRecieverValidationCount(0);
                        transactionLoadVO.setTotalSenderTopupFailCount(0);
                        transactionLoadVO.setTotalSenderValFailCount(0);
                        transactionLoadVO.setTotalSenderValidationCount(0);
                        ArrayList newAlternateList = ((TransactionLoadVO) tempHash.get(key)).getAlternateServiceLoadType();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("newAlternateList=");
                        	loggerValue.append(newAlternateList);
                            _log.debug(methodName,loggerValue);
                        }

                        if (newAlternateList != null) {
                        	 int   newAlternateListSizes=newAlternateList.size();
                            for (int i = 0; i <newAlternateListSizes; i++) {
                                TransactionLoadVO newAlternateTransactionLoadVO = (TransactionLoadVO) newAlternateList.get(i);
                                boolean isRecordFound = false;

                                ArrayList alternateList = transactionLoadVO.getAlternateServiceLoadType();
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append("alternateList=");
                                	loggerValue.append(alternateList);
                                    _log.debug(methodName,loggerValue);
                                }

                                if (alternateList != null) {
                                	 int alternateListSizes=alternateList.size();
                                    for (int j = 0; j <alternateListSizes ; j++) {
                                        alternateTransactionLoadVO = (TransactionLoadVO) alternateList.get(i);
                                        if (_log.isDebugEnabled()) {
                                    		loggerValue.setLength(0);
                                        	loggerValue.append("Old Service Type=");
                                        	loggerValue.append(alternateTransactionLoadVO.getServiceType());
                                    		loggerValue.append(" New Service type=");
                                        	loggerValue.append(newAlternateTransactionLoadVO.getServiceType());
                                            _log.debug(methodName,loggerValue);
                                        }

                                        if (alternateTransactionLoadVO.getServiceType().equals(newAlternateTransactionLoadVO.getServiceType())) {
                                            isRecordFound = true;
                                            alternateTransactionLoadVO.setCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setFirstRequestCount(0);
                                            alternateTransactionLoadVO.setFirstRequestTime(0);
                                            alternateTransactionLoadVO.setLastInitializationTime(date);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setNoOfRequestSameSec(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setRecievedCount(0);
                                            alternateTransactionLoadVO.setRequestCount(0);
                                            alternateTransactionLoadVO.setRequestTimeoutSec(0);
                                            alternateTransactionLoadVO.setTotalRefusedCount(0);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setLastTxnProcessStartTime(t);
                                            alternateTransactionLoadVO.setCurrentRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderValidationCount(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setTotalInternalFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValidationCount(0);
                                            break;
                                        }

                                    }
                                    if (!isRecordFound) {
                                        if (_log.isDebugEnabled()) {
                                    		loggerValue.setLength(0);
                                        	loggerValue.append("Not found in ");
                                        	loggerValue.append(newAlternateTransactionLoadVO.getServiceType());
                                    		loggerValue.append(" In Old List thus adding it");
                                            _log.debug(methodName,loggerValue);
                                        }

                                        alternateList.add(newAlternateTransactionLoadVO);
                                        transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                    }
                                } else {
                                    if (_log.isDebugEnabled()) {
                                		loggerValue.setLength(0);
                                    	loggerValue.append("Previous Alternate List Empty Thus Add Key  ");
                                    	loggerValue.append(newAlternateTransactionLoadVO.getServiceType());
                                		loggerValue.append(" In Old List");
                                        _log.debug(methodName,loggerValue);
                                    }
                                    alternateList = new ArrayList();
                                    alternateList.add(newAlternateTransactionLoadVO);
                                    transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                }
                            }
                        } else {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Setting new List as it is Empty in new List");
                            }
                            transactionLoadVO.setAlternateServiceLoadType(((TransactionLoadVO) tempHash.get(key)).getAlternateServiceLoadType());
                        }
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                             RedisActivityLog.log("LoadControllerCache->initializeTransactionLoad->Start");                       
                             jedis = RedisConnectionPool.getPoolInstance().getResource();
                             jedis.hset(hKeyTXNLoadHash,key,gson.toJson(transactionLoadVO));
                             RedisActivityLog.log("LoadControllerCache->initializeTransactionLoad->End");                       
                        }
                    } else {
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append( "Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _transactionLoadHash=" );
                        	loggerValue.append(_transactionLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }

                	   if (_transactionLoadHash == null) {
                           _transactionLoadHash = new Hashtable();
                       }
                       _transactionLoadHash.put(key, tempHash.get(key));
	                    if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                    	 RedisActivityLog.log("LoadControllerCache->initializeTransactionLoad->Start");                       
                         jedis = RedisConnectionPool.getPoolInstance().getResource();
                         jedis.hset(hKeyTXNLoadHash,key,gson.toJson(transactionLoadVO));
                         RedisActivityLog.log("LoadControllerCache->initializeTransactionLoad->End");  
	                    } 
                      if (hashMap == null) {
                          hashMap = new HashMap();
                      }
                      if (!hashMap.containsKey(key)) {
                          hashMap.put(key, new TransactionLoadController());
                      }
                      LoadControllerCache.setTransactionLoadObjectMap(hashMap);
	               }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_transactionLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		} catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
        }finally{
			if(jedis != null)
				jedis.close();
		}
    }

    /**
     * For updating Instance Load
     * 
     * @param p_instanceID
     */
    public static void updateInstanceLoad(String p_instanceID) {
        final String methodName = "updateInstanceLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: with p_instanceID=");
        	loggerValue.append(p_instanceID);
            _log.debug(methodName,loggerValue);
        }
        Jedis jedis = null;
        try {
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,InstanceLoadVO> tempHash = loadControllerDAO.loadInstanceLoadDetails(p_instanceID);
            InstanceLoadVO instanceLoadVO = null;
            InstanceLoadVO newInstanceLoadVO = null;
            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    newInstanceLoadVO = (InstanceLoadVO) tempHash.get(key);
                    if (getInstanceLoadHash() != null && getInstanceLoadHash().containsKey(key)) {
                        instanceLoadVO = (InstanceLoadVO) getInstanceLoadHash().get(key);
                        instanceLoadVO.setInstanceName(newInstanceLoadVO.getInstanceName());
                        instanceLoadVO.setCurrentStatus(newInstanceLoadVO.getCurrentStatus());
                        instanceLoadVO.setHostAddress(newInstanceLoadVO.getHostAddress());
                        instanceLoadVO.setHostPort(newInstanceLoadVO.getHostPort());
                        instanceLoadVO.setDefinedTransactionLoad(newInstanceLoadVO.getDefinedTransactionLoad());
                        instanceLoadVO.setTransactionLoad(newInstanceLoadVO.getTransactionLoad());
                        instanceLoadVO.setRequestTimeoutSec(newInstanceLoadVO.getRequestTimeoutSec());
                        instanceLoadVO.setDefinedTPS(newInstanceLoadVO.getDefinedTPS());
                        instanceLoadVO.setDefualtTPS(newInstanceLoadVO.getDefualtTPS());
                        instanceLoadVO.setCurrentTPS(newInstanceLoadVO.getCurrentTPS());
                        _loadType = newInstanceLoadVO.getLoadType();
                        instanceLoadVO.setLoadType(_loadType);
                        instanceLoadVO.setInstanceLoadStatus(newInstanceLoadVO.isInstanceLoadStatus());
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                        	RedisActivityLog.log("LoadControllerCache->updateInstanceLoad->Start");
              	   		    jedis = RedisConnectionPool.getPoolInstance().getResource();
              				jedis.hset(hKeyinstanceLoadHash,key,gson.toJson(instanceLoadVO));
                      		RedisActivityLog.log("LoadControllerCache->updateInstanceLoad->End");
                        }
                    
                    } else {
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _instanceLoadHash=");
                        	loggerValue.append(_instanceLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }

                        if (_instanceLoadHash == null) {
                            _instanceLoadHash = new Hashtable();
                        }
                        _instanceLoadHash.put(key, tempHash.get(key));
                        
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                            RedisActivityLog.log("LoadControllerCache->updateInstanceLoad->Start");                       
                            jedis = RedisConnectionPool.getPoolInstance().getResource();
              				jedis.hset(hKeyinstanceLoadHash,key,gson.toJson(instanceLoadVO));
                            RedisActivityLog.log("LoadControllerCache->updateInstanceLoad->End");                       
	    	            }
                        if (_instanceLoadObjectMap == null) {
                            _instanceLoadObjectMap = new HashMap();
                        }
                        if (!_instanceLoadObjectMap.containsKey(_instanceID)) {
                            _instanceLoadObjectMap.put(_instanceID, new InstanceLoadController());
                        }
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_instanceLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[" + methodName+"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[" + methodName+"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[" + methodName+"]", "", "", "", "Exception:" + e.getMessage());
        }finally{
			if(jedis != null)
				jedis.close();
		}
     }

    /**
     * For updating Network Load
     * 
     */
    public static void updateNetworkLoadDetails() {
        final String methodName = "updateNetworkLoadDetails";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,NetworkLoadVO> tempHash = loadControllerDAO.loadNetworkLoadDetails(_instanceID, _loadType, false);
            NetworkLoadVO networkLoadVO = null;
            NetworkLoadVO newNetworkLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getNetworkLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    newNetworkLoadVO = (NetworkLoadVO) tempHash.get(key);
                    if (getNetworkLoadHash() != null && getNetworkLoadHash().containsKey(key)) {
                        networkLoadVO = (NetworkLoadVO) getNetworkLoadHash().get(key);
                        networkLoadVO.setInstanceID(newNetworkLoadVO.getInstanceID());
                        networkLoadVO.setNetworkCode(newNetworkLoadVO.getNetworkCode());
                        networkLoadVO.setDefinedTransactionLoad(newNetworkLoadVO.getDefinedTransactionLoad());
                        networkLoadVO.setTransactionLoad(newNetworkLoadVO.getTransactionLoad());
                        networkLoadVO.setRequestTimeoutSec(newNetworkLoadVO.getRequestTimeoutSec());
                        networkLoadVO.setDefinedTPS(newNetworkLoadVO.getDefinedTPS());
                        networkLoadVO.setDefualtTPS(newNetworkLoadVO.getDefualtTPS());
                        networkLoadVO.setCurrentTPS(newNetworkLoadVO.getCurrentTPS());
                        networkLoadVO.setC2sInstanceID(newNetworkLoadVO.getC2sInstanceID());
                        networkLoadVO.setP2pInstanceID(newNetworkLoadVO.getP2pInstanceID());
                        networkLoadVO.setLoadType(_loadType);
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                     		RedisActivityLog.log("LoadControllerCache->updateNetworkLoad->Start");
                     	    jedis = RedisConnectionPool.getPoolInstance().getResource();
                     		jedis.hset(hKeyNetworkLoadHash,key,gson.toJson(networkLoadVO));
                     		RedisActivityLog.log("LoadControllerCache->updateNetworkLoad->End");
                          }
                    } else {
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _networkLoadHash=");
                        	loggerValue.append(_networkLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }
                    
                        if (_networkLoadHash == null) {
                            _networkLoadHash = new Hashtable();
                        }
                        _networkLoadHash.put(key, tempHash.get(key));
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                     		RedisActivityLog.log("LoadControllerCache->updateNetworkLoad->Start");
                     	    jedis = RedisConnectionPool.getPoolInstance().getResource();
                     		jedis.hset(hKeyNetworkLoadHash,key,gson.toJson(tempHash.get(key)));
                     		RedisActivityLog.log("LoadControllerCache->updateNetworkLoad->End");
                          }
                        if (hashMap == null) {
                            hashMap = new HashMap();
                        }
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new NetworkLoadController());
                            LoadControllerCache.setNetworkLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_networkLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[" + methodName+"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[" + methodName+"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		} catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[" + methodName+"]", "", "", "", "Exception:" + e.getMessage());
        }finally{
			if(jedis != null)
				jedis.close();
		}
    }

    /**
     * For updating interface Load
     * 
     */
    public static void updateInterfaceLoadDetails() {
        final String methodName = "updateInterfaceLoadDetails";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,InterfaceLoadVO> tempHash = loadControllerDAO.loadInterfaceLoadDetails(_instanceID, _loadType, false);
            InterfaceLoadVO interfaceLoadVO = null;
            InterfaceLoadVO newInterfaceLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getInterfaceLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    newInterfaceLoadVO = (InterfaceLoadVO) tempHash.get(key);
                    if (getInterfaceLoadHash() != null && getInterfaceLoadHash().containsKey(key)) {
                        interfaceLoadVO = (InterfaceLoadVO) getInterfaceLoadHash().get(key);
                        interfaceLoadVO.setInstanceID(newInterfaceLoadVO.getInstanceID());
                        interfaceLoadVO.setNetworkCode(newInterfaceLoadVO.getNetworkCode());
                        interfaceLoadVO.setInterfaceID(newInterfaceLoadVO.getInterfaceID());
                        interfaceLoadVO.setTransactionLoad(newInterfaceLoadVO.getTransactionLoad());
                        interfaceLoadVO.setRequestTimeoutSec(newInterfaceLoadVO.getRequestTimeoutSec());
                        interfaceLoadVO.setDefualtTPS(newInterfaceLoadVO.getDefualtTPS());
                        interfaceLoadVO.setCurrentTPS(newInterfaceLoadVO.getCurrentTPS());
                        interfaceLoadVO.setQueueSize(newInterfaceLoadVO.getQueueSize());
                        interfaceLoadVO.setQueueTimeOut(newInterfaceLoadVO.getQueueTimeOut());
                        interfaceLoadVO.setNextQueueCheckCaseAfterSec(newInterfaceLoadVO.getNextQueueCheckCaseAfterSec());
                        interfaceLoadVO.setLoadType(_loadType);
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                            RedisActivityLog.log("LoadControllerCache->updateInterfaceLoadDetails->Start");                       
                            jedis = RedisConnectionPool.getPoolInstance().getResource();
                            jedis.hset(hKeyInterfaceLoadHash,key,gson.toJson(interfaceLoadVO));
                             RedisActivityLog.log("LoadControllerCache->updateInterfaceLoadDetails->End");                       
                  		}
                    } else {
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _interfaceLoadHash=");
                        	loggerValue.append(_interfaceLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }
                       
                        if (_interfaceLoadHash == null) {
                            _interfaceLoadHash = new Hashtable();
                        }
                        _interfaceLoadHash.put(key, tempHash.get(key));
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                    	   RedisActivityLog.log("LoadControllerCache->updateInterfaceLoadDetails->Start");                       
                           jedis = RedisConnectionPool.getPoolInstance().getResource();
                           jedis.hset(hKeyInterfaceLoadHash,key,gson.toJson(interfaceLoadVO));
                            RedisActivityLog.log("LoadControllerCache->updateInterfaceLoadDetails->End");                       
                        }
                        if (hashMap == null) {
                            hashMap = new HashMap();
                        }
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new InterfaceLoadController());
                            LoadControllerCache.setInterfaceLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_interfaceLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	 		loggerValue.setLength(0);
	    	loggerValue.append("Exception ");
	    	loggerValue.append(e.getMessage());
	        _log.error(methodName,loggerValue);
	        _log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	    }
	   finally{
			if(jedis != null)
				jedis.close();
		 } 
    }

    /**
     * For updating Transaction Load
     * 
     */
    public static void updateTransactionLoadDetails() {
        final String methodName = "updateTransactionLoadDetails";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,TransactionLoadVO> tempHash = loadControllerDAO.loadTransactionLoadDetails(_instanceID, _loadType, false);
            TransactionLoadVO transactionLoadVO = null;
            TransactionLoadVO newTransactionLoadVO = null;
            TransactionLoadVO alternateTransactionLoadVO = null;
            TransactionLoadVO newAlternateTransactionLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getTransactionLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    newTransactionLoadVO = (TransactionLoadVO) tempHash.get(key);
                    if (getTransactionLoadHash() != null && getTransactionLoadHash().containsKey(key)) {
                        transactionLoadVO = (TransactionLoadVO) getInstanceLoadHash().get(key);
                        transactionLoadVO.setInstanceID(newTransactionLoadVO.getInstanceID());
                        transactionLoadVO.setNetworkCode(newTransactionLoadVO.getNetworkCode());
                        transactionLoadVO.setInterfaceID(newTransactionLoadVO.getInterfaceID());
                        transactionLoadVO.setServiceType(newTransactionLoadVO.getServiceType());
                        transactionLoadVO.setDefinedTransactionLoad(newTransactionLoadVO.getDefinedTransactionLoad());
                        transactionLoadVO.setTransactionLoad(newTransactionLoadVO.getTransactionLoad());
                        transactionLoadVO.setRequestTimeoutSec(newTransactionLoadVO.getRequestTimeoutSec());
                        transactionLoadVO.setDefinedTPS(newTransactionLoadVO.getDefinedTPS());
                        transactionLoadVO.setDefualtTPS(newTransactionLoadVO.getDefualtTPS());
                        transactionLoadVO.setCurrentTPS(newTransactionLoadVO.getCurrentTPS());
                        transactionLoadVO.setMinimumServiceTime(newTransactionLoadVO.getMinimumServiceTime());
                        transactionLoadVO.setDefinedOverFlowCount(newTransactionLoadVO.getDefinedOverFlowCount());
                        transactionLoadVO.setNextCheckTimeOutCaseAfterSec(newTransactionLoadVO.getNextCheckTimeOutCaseAfterSec());
                        transactionLoadVO.setLoadType(_loadType);
                        ArrayList newAlternateList = newTransactionLoadVO.getAlternateServiceLoadType();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("newAlternateList=");
                        	loggerValue.append(newAlternateList);
                            _log.debug(methodName,loggerValue);
                        }

                        if (newAlternateList != null) {
                            for (int i = 0; i < newAlternateList.size(); i++) {
                                newAlternateTransactionLoadVO = (TransactionLoadVO) newAlternateList.get(i);
                                boolean isRecordFound = false;

                                ArrayList alternateList = transactionLoadVO.getAlternateServiceLoadType();
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append("alternateList=" );
                                	loggerValue.append(alternateList);
                                    _log.debug(methodName,loggerValue);
                                }

                                if (alternateList != null) {
                                    for (int j = 0; j < alternateList.size(); j++) {
                                        alternateTransactionLoadVO = (TransactionLoadVO) alternateList.get(i);
                                        if (_log.isDebugEnabled()) {
                                    		loggerValue.setLength(0);
                                        	loggerValue.append("Old Service Type=");
                                        	loggerValue.append(alternateTransactionLoadVO.getServiceType());
                                        	loggerValue.append(" New Service type=");
                                        	loggerValue.append(newAlternateTransactionLoadVO.getServiceType());
                                            _log.debug(methodName, loggerValue);
                                        }

                                        if (alternateTransactionLoadVO.getServiceType().equals(newAlternateTransactionLoadVO.getServiceType())) {
                                            isRecordFound = true;
                                            alternateTransactionLoadVO.setDefinedTransactionLoad(newAlternateTransactionLoadVO.getDefinedTransactionLoad());
                                            alternateTransactionLoadVO.setTransactionLoad(newAlternateTransactionLoadVO.getTransactionLoad());
                                            alternateTransactionLoadVO.setRequestTimeoutSec(newAlternateTransactionLoadVO.getRequestTimeoutSec());
                                            alternateTransactionLoadVO.setDefinedTPS(newAlternateTransactionLoadVO.getDefinedTPS());
                                            alternateTransactionLoadVO.setDefualtTPS(newAlternateTransactionLoadVO.getDefualtTPS());
                                            alternateTransactionLoadVO.setCurrentTPS(newAlternateTransactionLoadVO.getCurrentTPS());
                                            alternateTransactionLoadVO.setMinimumServiceTime(newAlternateTransactionLoadVO.getMinimumServiceTime());
                                            alternateTransactionLoadVO.setOverFlowCount(newAlternateTransactionLoadVO.getOverFlowCount());
                                            alternateTransactionLoadVO.setNextCheckTimeOutCaseAfterSec(newAlternateTransactionLoadVO.getNextCheckTimeOutCaseAfterSec());
                                            alternateTransactionLoadVO.setLoadType(_loadType);
                                            break;
                                        }

                                    }
                                    if (!isRecordFound) {
                                        if (_log.isDebugEnabled()) {
                                    		loggerValue.setLength(0);
                                        	loggerValue.append("Not found in ");
                                        	loggerValue.append(newAlternateTransactionLoadVO.getServiceType());
                                    		loggerValue.append( " In Old List thus adding it");
                                            _log.debug(methodName, loggerValue);
                                        }

                                        alternateList.add(newAlternateTransactionLoadVO);
                                        transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                    }
                                } else {
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(methodName, "Previous Alternate List Empty Thus Add Key  " + newAlternateTransactionLoadVO.getServiceType() + " In Old List");
                                    }

                                    alternateList = new ArrayList();
                                    alternateList.add(newAlternateTransactionLoadVO);
                                    transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                }
                            }
                        } else {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Setting new List as it is Empty in new List");
                            }

                            transactionLoadVO.setAlternateServiceLoadType(newTransactionLoadVO.getAlternateServiceLoadType());
                        }
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                            RedisActivityLog.log("LoadControllerCache->updateTransactionLoadDetails->Start");                       
                            jedis = RedisConnectionPool.getPoolInstance().getResource();
                            jedis.hset(hKeyTXNLoadHash,key,gson.toJson(transactionLoadVO));
                            RedisActivityLog.log("LoadControllerCache->updateTransactionLoadDetails->End");                       
                 		}
                    } else {

                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _transactionLoadHash=" );
                        	loggerValue.append(_transactionLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName, loggerValue);
                        }
                        if (_transactionLoadHash == null) {
                            _transactionLoadHash = new Hashtable();
                        }
                        _transactionLoadHash.put(key, tempHash.get(key));
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                        	RedisActivityLog.log("LoadControllerCache->updateTransactionLoadDetails->Start");                       
                            jedis = RedisConnectionPool.getPoolInstance().getResource();
                            jedis.hset(hKeyTXNLoadHash,key,gson.toJson(transactionLoadVO));
                            RedisActivityLog.log("LoadControllerCache->updateTransactionLoadDetails->End");                       
	    	             }
                        if (hashMap == null) {
                            hashMap = new HashMap();
                        }
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new TransactionLoadController());
                            LoadControllerCache.setTransactionLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append( _transactionLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }  catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception " );
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateTransactionLoadDetails]", "", "", "", "Exception:" + e.getMessage());
	        }finally {
	        	if(jedis != null)
					jedis.close();
        	}
            }

    /**
     * @return Returns the networkServiceLoadHash.
     */
    public static Hashtable getNetworkServiceLoadHash() {
    	String methodName = "getNetworkServiceLoadHash";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		HashMap<String,NetworkServiceLoadVO>insMap = networkServiceLoadhashMap.get(hKeyNetServiceLoadHash);
				if(insMap != null){
					updateLocalNetServiceLoadHash(_networkServiceLoadHash,insMap);
				}
        	}catch(ExecutionException je){
    			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    	        _log.errorTrace(methodName, je);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "ExecutionException :" + je.getMessage());
    		}catch(InvalidCacheLoadException  ex){
    			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
    		}catch (Exception e){
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    	        _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
    		}
    	}
        
        return _networkServiceLoadHash;
    }

    /**
     * @param networkServiceLoadHash
     *            The networkServiceLoadHash to set.
     */
    public static void setNetworkServiceLoadHash(Hashtable networkServiceLoadHash) {
        _networkServiceLoadHash = networkServiceLoadHash;
    }

    /**
     * @return Returns the networkServiceLoadHash.
     */
    public static Hashtable getNetworkServiceHourlyLoadHash() {
    	String methodName = "getNetworkServiceHourlyLoadHash";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		HashMap<String,NetworkServiceHourlyLoadVO>insMap = netServiceHourlyLoadhashMap.get(hKeyNetServiceHourHash);
				if(insMap != null){
					updateLocalNetServiceHourLoadHash(_networkServiceHourlyLoadHash, insMap);
				}
        	}catch(ExecutionException je){
    			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    	        _log.errorTrace(methodName, je);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "ExecutionException :" + je.getMessage());
    		}catch(InvalidCacheLoadException  ex){
    			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
    		}catch (Exception e){
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    	        _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception :" + e.getMessage());
    		}
    	}
        return _networkServiceHourlyLoadHash;
    }

    /**
     * @param networkServiceLoadHash
     *            The networkServiceLoadHash to set.
     */
    public static void setNetworkServiceHourlyLoadHash(Hashtable networkServiceHourlyLoadHash) {
        _networkServiceHourlyLoadHash = networkServiceHourlyLoadHash;
    }

    public static void refreshNetworkServiceCounters(String p_instanceID) // Pass
    // from
    // config
    // servlets
    {
        final String methodName = "refreshNetworkServiceCounters";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: ");
        }
        Jedis jedis = null;
        try {
            _instanceID = p_instanceID;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Before loading:" + _networkServiceLoadHash.size());
            }
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
       	   _networkServiceLoadHash = loadControllerDAO.loadNetworkSeriveDetails(p_instanceID);
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                RedisActivityLog.log("LoadControllerCache->refreshNetworkServiceCounters->Start"); 
                jedis = RedisConnectionPool.getPoolInstance().getResource();
                Pipeline pipeline = jedis.pipelined();
   				Set<String> keys = _networkServiceLoadHash.keySet();
				 Iterator<String> itr = keys.iterator();
				 while (itr.hasNext()) { 
			       String str = itr.next();
			       pipeline.hset(hKeyNetServiceLoadHash, str,gson.toJson(_networkServiceLoadHash.get(str)));
				 }
				 pipeline.sync();
             RedisActivityLog.log("LoadControllerCache->refreshNetworkServiceCounters->End");                       
         }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " After loading:" + _networkServiceLoadHash.size());
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
	         _log.error(methodName, "Exception " + e.getMessage());
	         _log.errorTrace(methodName, e);
	         EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	     }finally{
			if(jedis != null)
				jedis.close();
		 } 
    }

    public static void refreshNetworkServiceHourlyCounters(String p_instanceID) // Pass
    // from
    // config
    // servlets
    {
        final String methodName = "refreshNetworkServiceHourlyCounters";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: ");
        }
        Jedis jedis = null;
        try {
            _instanceID = p_instanceID;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Before loading:" + _networkServiceHourlyLoadHash.size());
            }
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
        	_networkServiceHourlyLoadHash = loadControllerDAO.loadNetworkServiceHourlyDetails(p_instanceID);
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                RedisActivityLog.log("LoadControllerCache->refreshNetworkServiceHourlyCounters->Start");                       
                jedis = RedisConnectionPool.getPoolInstance().getResource();
                Pipeline pipeline = jedis.pipelined();
                Set<Entry<String, String>> entrySet = _networkServiceHourlyLoadHash.entrySet();
    		        Set<String> keys = _networkServiceHourlyLoadHash.keySet();
    		        Iterator<String> itr = keys.iterator();
   				 	while (itr.hasNext()) { 
	   			       String str = itr.next();
    			       pipeline.hset(hKeyNetServiceHourHash, str, gson.toJson(_networkServiceHourlyLoadHash.get(str)));
   				  }
   				 pipeline.sync();
      		RedisActivityLog.log("LoadControllerCache->refreshNetworkServiceHourlyCounters->End");                             
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " After loading:" + _networkServiceHourlyLoadHash.size());
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	     }finally{
			if(jedis != null)
				jedis.close();
		 }
    }

    /**
     * Method to update network Service Load Details
     * 
     */
    public static void updateNetworkServiceLoadDetails() {
        final String methodName = "updateNetworkServiceLoadDetails";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,NetworkServiceLoadVO> tempHash = loadControllerDAO.loadNetworkSeriveDetails(_instanceID);
            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    if (getNetworkServiceLoadHash()!= null && !getNetworkServiceLoadHash().containsKey(key)) {
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =" );
                        	loggerValue.append(key);
                    		loggerValue.append(" in _networkServiceLoadHash=");
                        	loggerValue.append(_networkServiceLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
		                    RedisActivityLog.log("LoadControllerCache->updateNetworkServiceLoadDetails->Start");                             
		                    jedis = RedisConnectionPool.getPoolInstance().getResource();
		                    jedis.hset(hKeyNetServiceLoadHash, key, gson.toJson(tempHash.get(key)));
		                    RedisActivityLog.log("LoadControllerCache->updateNetworkServiceLoadDetails->End");                             
                	  	}
                        _networkServiceLoadHash.put(key, tempHash.get(key));
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_interfaceLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName, loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	      }finally{
			if(jedis != null)
				jedis.close();
		 }
    }

    /**
     * Method to initialize Network Service Load
     * 
     */
    public static void initializeNetworkServiceLoad() {
        final String methodName = "initializeNetworkServiceLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable<String,NetworkServiceLoadVO> tempHash = loadControllerDAO.loadNetworkSeriveDetails(_instanceID);
            NetworkServiceLoadVO networkServiceLoadVO = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    if (getNetworkServiceLoadHash() != null && getNetworkServiceLoadHash().containsKey(key)) {
                        networkServiceLoadVO = (NetworkServiceLoadVO) getNetworkServiceLoadHash().get(key);
                        networkServiceLoadVO.setRecievedCount(0);
                        networkServiceLoadVO.setAverageServiceTime(0);
                        networkServiceLoadVO.setBeforeGatewayFoundError(0);
                        networkServiceLoadVO.setLastInitializationTime(date);
                        networkServiceLoadVO.setLastReceievedTime(t);
                        networkServiceLoadVO.setBeforeNetworkFoundError(0);
                        networkServiceLoadVO.setBeforeServiceTypeFoundError(0);
                        networkServiceLoadVO.setFailCount(0);
                        networkServiceLoadVO.setLastRequestServiceTime(0);
                        networkServiceLoadVO.setOtherNetworkReqCount(0);
                        networkServiceLoadVO.setOthersFailCount(0);
                        networkServiceLoadVO.setSuccessCount(0);
                        networkServiceLoadVO.setUnderProcessCount(0);
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                        RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceLoad->Start1");                             
		                    RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceLoad->Start");                             
		                    jedis = RedisConnectionPool.getPoolInstance().getResource();
		                    jedis.hset(hKeyNetServiceLoadHash, key, gson.toJson(networkServiceLoadVO));
		                    RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceLoad->End");                             
                     
                        }
                    } else {
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _networkServiceLoadHash=");
                        	loggerValue.append(_networkServiceLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }
                        if (_networkServiceLoadHash == null) {
                            _networkServiceLoadHash = new Hashtable();
                        }
                        _networkServiceLoadHash.put(key, tempHash.get(key));
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
 		                    RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceLoad->Start");                             
 		                    jedis = RedisConnectionPool.getPoolInstance().getResource();
 		                    jedis.hset(hKeyNetServiceLoadHash, key, gson.toJson(tempHash.get(key)));
 		                    RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceLoad->End");                             
                        }
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_networkServiceLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
		    _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		    _log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	     }finally{
			if(jedis != null)
				jedis.close();
		 }
    }

    /**
     * Method to initialize Network Service Load
     * 
     */
    public static void initializeNetworkServiceHourlyLoad() {
        final String methodName = "initializeNetworkServiceHourlyLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadNetworkServiceHourlyDetails(_instanceID);
            NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append("Got Key =");
                    	loggerValue.append(key);
                        _log.debug(methodName,loggerValue);
                    }
                    if (getNetworkServiceHourlyLoadHash() != null && getNetworkServiceHourlyLoadHash().containsKey(key)) {
                        networkServiceHourlyLoadVO = (NetworkServiceHourlyLoadVO) getNetworkServiceHourlyLoadHash().get(key);

                        networkServiceHourlyLoadVO.setLastInitializationTime(date);
                        networkServiceHourlyLoadVO.setLastReceievedTime(t);

                        networkServiceHourlyLoadVO.setFailCount(0);

                        networkServiceHourlyLoadVO.setSuccessCount(0);
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                            RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceHourlyLoad->Start");                             
                            jedis = RedisConnectionPool.getPoolInstance().getResource();
                            jedis.hset(hKeyNetServiceHourHash, key,gson.toJson(networkServiceHourlyLoadVO));
                            RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceHourlyLoad->End");                             
                  	  	}
                    } else {

                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("Not found Key =");
                        	loggerValue.append(key);
                    		loggerValue.append(" in _networkServiceHourlyLoadHash=");
                        	loggerValue.append(_networkServiceHourlyLoadHash);
                        	loggerValue.append(" Thus inserting the same");
                            _log.debug(methodName,loggerValue);
                        }
                        if (_networkServiceHourlyLoadHash == null) {
                            _networkServiceHourlyLoadHash = new Hashtable();
                        }
                        _networkServiceHourlyLoadHash.put(key, tempHash.get(key));
                        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                            RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceHourlyLoad->Start");                             
                            jedis = RedisConnectionPool.getPoolInstance().getResource();
                            jedis.hset(hKeyNetServiceHourHash, key,gson.toJson(tempHash.get(key)));
                            RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceHourlyLoad->End");                             
                  	  	}
                    }
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" After loading:");
            	loggerValue.append(_networkServiceHourlyLoadHash.size());
                _log.debug(methodName,loggerValue);
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	     }finally{
			if(jedis != null)
				jedis.close();
		 }
    }

    /**
     * Method to initialize the network service load
     * 
     * @param p_instanceID
     * @param p_reqType
     * @param p_networkID
     * @param p_serviceType
     */
    public static void initializeNetworkServiceLoad(String p_instanceID, String p_reqType, String p_networkID, String p_serviceType) {
        final String methodName = "initializeNetworkServiceLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: with p_instanceID=");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_reqType=");
        	loggerValue.append(p_reqType);
        	loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
    		loggerValue.append("p_serviceType=");
        	loggerValue.append(p_serviceType);
            _log.debug(methodName,loggerValue);
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            if (getNetworkServiceLoadHash() != null) {
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" Before loading:");
                	loggerValue.append(getNetworkServiceLoadHash().size());
                    _log.debug(methodName,loggerValue);
                }
                Enumeration size = getNetworkServiceLoadHash().keys();
                while (size.hasMoreElements()) {
                    NetworkServiceLoadVO networkServiceLoadVO = (NetworkServiceLoadVO) getNetworkServiceLoadHash().get(size.nextElement());
                    String instanceID = networkServiceLoadVO.getInstanceID();
                    String reqType = networkServiceLoadVO.getGatewayType();
                    String networkID = networkServiceLoadVO.getNetworkCode();
                    String serviceType = networkServiceLoadVO.getServiceType();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || reqType.equals(p_networkID)) {
                            if ("ALL".equals(p_reqType) || networkID.equals(p_reqType)) {
                                if ("ALL".equals(p_serviceType) || serviceType.equals(p_serviceType)) {
                                    networkServiceLoadVO.setRecievedCount(0);
                                    networkServiceLoadVO.setAverageServiceTime(0);
                                    networkServiceLoadVO.setBeforeGatewayFoundError(0);
                                    networkServiceLoadVO.setLastInitializationTime(date);
                                    networkServiceLoadVO.setLastReceievedTime(t);
                                    networkServiceLoadVO.setBeforeNetworkFoundError(0);
                                    networkServiceLoadVO.setBeforeServiceTypeFoundError(0);
                                    networkServiceLoadVO.setFailCount(0);
                                    networkServiceLoadVO.setLastRequestServiceTime(0);
                                    networkServiceLoadVO.setOtherNetworkReqCount(0);
                                    networkServiceLoadVO.setOthersFailCount(0);
                                    networkServiceLoadVO.setSuccessCount(0);
                                    networkServiceLoadVO.setUnderProcessCount(0);
                                    if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                                        RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceLoad->Start");                             
                                        jedis = RedisConnectionPool.getPoolInstance().getResource();
                                        jedis.hset(hKeyNetServiceLoadHash, (String)size.nextElement(),gson.toJson(networkServiceLoadVO));
                                        RedisActivityLog.log("LoadControllerCache->initializeNetworkServiceLoad->End");                             
                              	  	}
                                }
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" After loading:");
                	loggerValue.append(_networkServiceLoadHash.size());
                    _log.debug(methodName,loggerValue);
                }
            }
        }catch(JedisConnectionException je){
		    _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		    _log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
        }finally{
			if(jedis != null)
				jedis.close();
		 }
    }

    /**
     * Method to initialize Network load
     * 
     * @param p_instanceID
     * @param p_networkID
     */
    public static void initializeNetworkLoad(String p_instanceID, String p_networkID) {
        final String methodName = "initializeNetworkLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: with p_instanceID=");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
            _log.debug(methodName, loggerValue);
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t1 = new Timestamp(date.getTime());

            if (getNetworkLoadHash() != null) {
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append( " Before loading:");
                	loggerValue.append(_networkLoadHash.size());
                    _log.debug(methodName,loggerValue);
                }
                Enumeration size = getNetworkLoadHash().keys();
                while (size.hasMoreElements()) {
                    NetworkLoadVO networkLoadVO = (NetworkLoadVO) getNetworkLoadHash().get(size.nextElement());
                    String instanceID = networkLoadVO.getInstanceID();
                    String networkID = networkLoadVO.getNetworkCode();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            networkLoadVO.setCurrentTransactionLoad(0);
                            networkLoadVO.setFirstRequestCount(0);
                            networkLoadVO.setFirstRequestTime(0);
                            networkLoadVO.setLastInitializationTime(date);
                            networkLoadVO.setLastReceievedTime(t1);
                            networkLoadVO.setLastRefusedTime(t1);
                            networkLoadVO.setNoOfRequestSameSec(0);
                            networkLoadVO.setReceiverCurrentTransactionLoad(0);
                            networkLoadVO.setRecievedCount(0);
                            networkLoadVO.setRequestCount(0);
                            networkLoadVO.setRequestTimeoutSec(0);
                            networkLoadVO.setTotalRefusedCount(0);
                            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	                     		RedisActivityLog.log("LoadControllerCache->initializeNetworkLoad->Start");
	                     	    jedis = RedisConnectionPool.getPoolInstance().getResource();
	                     		jedis.hset(hKeyNetworkLoadHash,(String)size.nextElement(),gson.toJson(networkLoadVO));
	                     		RedisActivityLog.log("LoadControllerCache->initializeNetworkLoad->End");
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" After loading:");
                	loggerValue.append(_networkLoadHash.size());
                    _log.debug(methodName,loggerValue);
                }
            }
         }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName, loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	        }finally{
			if(jedis != null)
				jedis.close();
		 }
    }

    /**
     * Method to initialize Interface load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     */
    public static void initializeInterfaceLoad(String p_instanceID, String p_networkID, String p_interfaceID) {
        final String methodName = "initializeInterfaceLoad";
        if (_log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: with p_instanceID=");
        	msg.append(p_instanceID);
        	msg.append(" p_networkID=");
        	msg.append(p_networkID);
        	msg.append("p_interfaceID=");
        	msg.append(p_interfaceID);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            if (getInterfaceLoadHash() != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, " Before loading:" + getInterfaceLoadHash().size());
                }
                Enumeration size = getInterfaceLoadHash().keys();
                while (size.hasMoreElements()) {
                    InterfaceLoadVO interfaceLoadVO = (InterfaceLoadVO) getInterfaceLoadHash().get(size.nextElement());
                    String instanceID = interfaceLoadVO.getInstanceID();
                    String networkID = interfaceLoadVO.getNetworkCode();
                    String interfaceID = interfaceLoadVO.getInterfaceID();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                interfaceLoadVO.setCurrentTransactionLoad(0);
                                interfaceLoadVO.setFirstRequestCount(0);
                                interfaceLoadVO.setFirstRequestTime(0);
                                interfaceLoadVO.setLastInitializationTime(date);
                                interfaceLoadVO.setLastReceievedTime(t);
                                interfaceLoadVO.setLastRefusedTime(t);
                                interfaceLoadVO.setNoOfRequestSameSec(0);
                                interfaceLoadVO.setReceiverCurrentTransactionLoad(0);
                                interfaceLoadVO.setRecievedCount(0);
                                interfaceLoadVO.setRequestCount(0);
                                interfaceLoadVO.setRequestTimeoutSec(0);
                                interfaceLoadVO.setTotalRefusedCount(0);
                                interfaceLoadVO.setCurrentQueueSize(0);
                                interfaceLoadVO.setLastQueueAdditionTime(t);
                                interfaceLoadVO.setLastQueueAdditionTime(t);
                                interfaceLoadVO.setLastQueueCaseCheckTime(0);
                                interfaceLoadVO.setLastReceievedTime(t);
                                interfaceLoadVO.setLastRefusedTime(t);
                                interfaceLoadVO.setLastTxnProcessStartTime(t);
                                if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                                RedisActivityLog.log("LoadControllerCache->initializeInterfaceLoad->Start");                             
                       			  jedis = RedisConnectionPool.getPoolInstance().getResource();
                       			  jedis.hset(hKeyInterfaceLoadHash, (String)size.nextElement(),gson.toJson(interfaceLoadVO));
                                 RedisActivityLog.log("LoadControllerCache->initializeInterfaceLoad->End");                             
                        	  	}
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, " After loading:" + _interfaceLoadHash.size());
                }
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	            _log.error(methodName, "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	     }finally{
			if(jedis != null)
				jedis.close();
		 }    }

    /**
     * Method to initialize Transaction load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     */
    public static void initializeTransactionLoad(String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType) {
        final String methodName = "initializeTransactionLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: with p_instanceID=");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
        	loggerValue.append("p_interfaceID=");
        	loggerValue.append(p_interfaceID);
    		loggerValue.append("p_serviceType=");
        	loggerValue.append(p_serviceType);
            _log.debug(methodName, loggerValue);
        }
        Jedis jedis = null;
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            if (getTransactionLoadHash() != null) {
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" Before loading:");
                	loggerValue.append(getTransactionLoadHash().size());
                    _log.debug(methodName,loggerValue);
                }
                Enumeration size = getTransactionLoadHash().keys();
                TransactionLoadVO alternateTransactionLoadVO = null;
               
                while (size.hasMoreElements()) {
                    TransactionLoadVO transactionLoadVO = (TransactionLoadVO) getTransactionLoadHash().get(size.nextElement());
                    String instanceID = transactionLoadVO.getInstanceID();
                    String networkID = transactionLoadVO.getNetworkCode();
                    String interfaceID = transactionLoadVO.getInterfaceID();
                    String serviceType = transactionLoadVO.getServiceType();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                if ("ALL".equals(p_serviceType) || serviceType.equals(p_serviceType)) {
                                    transactionLoadVO.setCurrentTransactionLoad(0);
                                    transactionLoadVO.setFirstRequestCount(0);
                                    transactionLoadVO.setFirstRequestTime(0);
                                    transactionLoadVO.setLastInitializationTime(date);
                                    transactionLoadVO.setLastReceievedTime(t);
                                    transactionLoadVO.setLastRefusedTime(t);
                                    transactionLoadVO.setNoOfRequestSameSec(0);
                                    transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                    transactionLoadVO.setRecievedCount(0);
                                    transactionLoadVO.setRequestCount(0);
                                    transactionLoadVO.setRequestTimeoutSec(0);
                                    transactionLoadVO.setTotalRefusedCount(0);
                                    transactionLoadVO.setLastReceievedTime(t);
                                    transactionLoadVO.setLastRefusedTime(t);
                                    transactionLoadVO.setLastTxnProcessStartTime(t);
                                    transactionLoadVO.setCurrentRecieverTopupCount(0);
                                    transactionLoadVO.setCurrentSenderTopupCount(0);
                                    transactionLoadVO.setCurrentRecieverValidationCount(0);
                                    transactionLoadVO.setCurrentSenderValidationCount(0);
                                    transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                    transactionLoadVO.setTotalInternalFailCount(0);
                                    transactionLoadVO.setTotalRecieverTopupCount(0);
                                    transactionLoadVO.setTotalRecieverTopupFailCount(0);
                                    transactionLoadVO.setTotalRecieverValFailCount(0);
                                    transactionLoadVO.setTotalRecieverValidationCount(0);
                                    transactionLoadVO.setTotalSenderTopupCount(0);
                                    transactionLoadVO.setTotalRecieverValidationCount(0);
                                    transactionLoadVO.setTotalSenderTopupFailCount(0);
                                    transactionLoadVO.setTotalSenderValFailCount(0);
                                    transactionLoadVO.setTotalSenderValidationCount(0);
                                    ArrayList alternateList = transactionLoadVO.getAlternateServiceLoadType();
                                    if (alternateList != null) {
                                    	 int alternateListSizes=alternateList.size();
                                        for (int i = 0; i <alternateListSizes ; i++) {
                                            alternateTransactionLoadVO = (TransactionLoadVO) alternateList.get(i);
                                            alternateTransactionLoadVO.setCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setFirstRequestCount(0);
                                            alternateTransactionLoadVO.setFirstRequestTime(0);
                                            alternateTransactionLoadVO.setLastInitializationTime(date);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setNoOfRequestSameSec(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setRecievedCount(0);
                                            alternateTransactionLoadVO.setRequestCount(0);
                                            alternateTransactionLoadVO.setRequestTimeoutSec(0);
                                            alternateTransactionLoadVO.setTotalRefusedCount(0);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setLastTxnProcessStartTime(t);
                                            alternateTransactionLoadVO.setCurrentRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderValidationCount(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setTotalInternalFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValidationCount(0);
                                        }
                                    }
                                    if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                                        RedisActivityLog.log("LoadControllerCache->initializeTransactionLoad->Start");                             
                                    	jedis = RedisConnectionPool.getPoolInstance().getResource();
	                       				jedis.hset(hKeyTXNLoadHash,(String)size.nextElement(),gson.toJson(transactionLoadVO));
                                        RedisActivityLog.log("LoadControllerCache->initializeTransactionLoad->End");                             
                            	  	}
                                }
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" After loading:");
                	loggerValue.append(_transactionLoadHash.size());
                    _log.debug(methodName,loggerValue);
                }
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeTransactionLoad]", "", "", "", "Exception:" + e.getMessage());
	        }finally {
	        	if(jedis != null)
					jedis.close();
        	}
        
    }

    /**
     * Method to update particular Network Load
     * 
     * @param p_instanceID
     * @param p_networkID
     */
    public static void updateNetworkLoadDetails(String p_instanceID, String p_networkID) {
        final String methodName = "updateNetworkLoadDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: with p_instanceID=");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
            _log.debug(methodName,loggerValue);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Jedis jedis = null;
        try {
            String loadType = null;
            if (getNetworkLoadHash() != null) {
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" Before loading:");
                	loggerValue.append(getNetworkLoadHash().size());
                    _log.debug(methodName,loggerValue);
                }
                Enumeration size = getNetworkLoadHash().keys();
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                while (size.hasMoreElements()) {
                    NetworkLoadVO networkLoadVO = (NetworkLoadVO) getNetworkLoadHash().get(size.nextElement());
                    String instanceID = networkLoadVO.getInstanceID();
                    String networkID = networkLoadVO.getNetworkCode();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            new LoadControllerDAO().updateNetworkLoadDetails(con, networkLoadVO, instanceID, networkID, loadType);
                            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
 	                     		RedisActivityLog.log("LoadControllerCache->updateNetworkLoadDetails->Start");
 	                     	    jedis = RedisConnectionPool.getPoolInstance().getResource();
 	                     		jedis.hset(hKeyNetworkLoadHash,(String)size.nextElement(),gson.toJson(networkLoadVO));
 	                     		RedisActivityLog.log("LoadControllerCache->updateNetworkLoadDetails->End");
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" After loading:");
                	loggerValue.append(_networkLoadHash.size());
                    _log.debug(methodName,loggerValue);
                }
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
        } finally {
        	if(jedis != null)
				jedis.close();
			if (mcomCon != null) {
				mcomCon.close("LoadControllerCache#updateNetworkLoadDetails");
				mcomCon = null;
		 }
        }
    }

    /**
     * Method to load particular Interface Load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     */
    public static void updateInterfaceLoadDetails(String p_instanceID, String p_networkID, String p_interfaceID) {
        final String methodName = "updateInterfaceLoadDetails";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: with p_instanceID=");
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
    		loggerValue.append("p_interfaceID=");
        	loggerValue.append(p_interfaceID);
            _log.debug(methodName, loggerValue);
        }
        Connection con = null;
        Jedis jedis = null;
        MComConnectionI mcomCon = null;
        try {
            String loadType = null;
            if (getInterfaceLoadHash() != null) {
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" Before loading:");
                	loggerValue.append(getInterfaceLoadHash().size());
                    _log.debug(methodName,loggerValue);
                }
                Enumeration size = getInterfaceLoadHash().keys();
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                while (size.hasMoreElements()) {
                    InterfaceLoadVO interfaceLoadVO = (InterfaceLoadVO) getInterfaceLoadHash().get(size.nextElement());
                    String instanceID = interfaceLoadVO.getInstanceID();
                    String networkID = interfaceLoadVO.getNetworkCode();
                    String interfaceID = interfaceLoadVO.getInterfaceID();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                new LoadControllerDAO().updateInterfaceLoadDetails(con, interfaceLoadVO, instanceID, networkID, interfaceID, loadType);
                                if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                                    RedisActivityLog.log("LoadControllerCache->updateInterfaceLoadDetails->Start");                             
                                    jedis = RedisConnectionPool.getPoolInstance().getResource();
                                    jedis.hset(hKeyInterfaceLoadHash, (String)size.nextElement(),gson.toJson(interfaceLoadVO));
                                     RedisActivityLog.log("LoadControllerCache->updateInterfaceLoadDetails->End");                             
                         	  	}
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" After loading:");
                	loggerValue.append(_interfaceLoadHash.size());
                    _log.debug(methodName,loggerValue);
                }
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateInterfaceLoadDetails]", "", "", "", "Exception:" + e.getMessage());
	        }finally {
	        	if(jedis != null)
					jedis.close();
		    	if(mcomCon != null){
		    		mcomCon.close("LoadControllerCache#updateInterfaceLoadDetails");
		    	mcomCon=null;
        	}
        }
    }

    /**
     * Method to load particular Transaction Load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     */
    public static void updateTransactionLoadDetails(String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType) {
        final String methodName = "updateTransactionLoadDetails";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: with p_instanceID=" );
        	loggerValue.append(p_instanceID);
    		loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
        	loggerValue.append("p_interfaceID=");
        	loggerValue.append(p_interfaceID);
    		loggerValue.append("p_serviceType=");
        	loggerValue.append(p_serviceType);
            _log.debug(methodName, loggerValue);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Jedis jedis = null;
        try {
            String loadType = null;
            if (getTransactionLoadHash() != null) {
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" Before loading:");
                	loggerValue.append(getTransactionLoadHash().size());
                    _log.debug(methodName,loggerValue);
                }
                Enumeration size = getTransactionLoadHash().keys();
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
             
                while (size.hasMoreElements()) {
                    TransactionLoadVO transactionLoadVO = (TransactionLoadVO) getTransactionLoadHash().get(size.nextElement());
                    String instanceID = transactionLoadVO.getInstanceID();
                    String networkID = transactionLoadVO.getNetworkCode();
                    String interfaceID = transactionLoadVO.getInterfaceID();
                    String serviceType = transactionLoadVO.getServiceType();
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append(" instanceID=");
                    	loggerValue.append(instanceID);
                		loggerValue.append(" networkID=");
                    	loggerValue.append(networkID);
                    	loggerValue.append(" interfaceID=");
                    	loggerValue.append(interfaceID);
                		loggerValue.append(" serviceType=");
                    	loggerValue.append(serviceType);
                        _log.debug(methodName,loggerValue);
                    }

                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                if ("ALL".equals(p_serviceType) || serviceType.equals(p_serviceType)) {
                                    new LoadControllerDAO().updateTransactionLoadDetails(con, transactionLoadVO, instanceID, networkID, interfaceID, serviceType, loadType);
                                    if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	                                    RedisActivityLog.log("LoadControllerCache->updateTransactionLoadDetails->Start");                             
                                    	jedis = RedisConnectionPool.getPoolInstance().getResource();
	                                     Pipeline pipeline = jedis.pipelined();
	                       				 jedis.hset(hKeyTXNLoadHash,(String)size.nextElement(),gson.toJson(transactionLoadVO));
	                                     RedisActivityLog.log("LoadControllerCache->updateTransactionLoadDetails->End");                             
                            	  	}
                                }
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append(" After loading:");
                	loggerValue.append(_transactionLoadHash.size());
                    _log.debug(methodName,loggerValue);
                }
            }
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 } catch (Exception e) {
	    		loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache["+ methodName + "]", "", "", "", "Exception:" + e.getMessage());
	        } finally {
	        	if(jedis != null)
					jedis.close();
	        	if (mcomCon != null) {
	        		mcomCon.close("LoadControllerCache#updateTransactionLoadDetails");
	        		mcomCon = null;
	        	}
	        }
	        	
    }

    /**
     * Method read. This method will read the object's last state from file .
     * 
     * @param file
     * @author nitin.rohilla
     * @return Hashtable
     */
    public static Hashtable read(File file) {
        Hashtable hashRead = null;
        try(FileInputStream fis = new FileInputStream(file);ObjectInputStream in = new ObjectInputStream(fis);) {
            
            hashRead = (Hashtable) in.readObject();
            in.close();
        } catch (Exception e) {
            final String methodName = "updateTransactionLoadDetails";
            _log.error(methodName, "Exception While Reading Object From File Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
        }
        return hashRead;
    }

    /**
     * Method modifyInstanceLoadVO this method is used to modify the
     * InstanceLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */
    private static void modifyInstanceLoadVO(Hashtable instanceLoad, Hashtable instanceHashRead) {
    	 InstanceLoadVO instanceLoadVO1 ;
         InstanceLoadVO instanceLoadVO2 ;
    	Set<Map.Entry<String, InstanceLoadVO >> entries =  instanceLoad.entrySet();
    	
    	for (Map.Entry<String, InstanceLoadVO> entry :  entries) {
    		 if (instanceHashRead.containsKey(entry.getKey())) {
    			 instanceLoadVO1 =  entry.getValue();
                 instanceLoadVO2 =  entry.getValue();
                 instanceLoadVO1.setRequestCount(instanceLoadVO2.getRequestCount());
                 instanceLoadVO1.setTotalRefusedCount(instanceLoadVO2.getTotalRefusedCount());
                 instanceLoadVO1.setLastReceievedTime(instanceLoadVO2.getLastReceievedTime());
                 instanceLoadVO1.setLastRefusedTime(instanceLoadVO2.getLastRefusedTime());
                 instanceLoadVO1.setLastTxnProcessStartTime(instanceLoadVO2.getLastTxnProcessStartTime());
                 instanceLoadVO1.setRecievedCount(instanceLoadVO2.getRecievedCount());
    		 }
    	}
    }

    /**
     * Method modifyNetworkLoadVO this method is used to modify the
     * NetworkLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */

    private static void modifyNetworkLoadVO(Hashtable networkLoad, Hashtable networkHashRead) {
      
    	 NetworkLoadVO networkVO1 ;
         NetworkLoadVO networkVO2 ;
    	Set<Map.Entry<String, NetworkLoadVO >> entries = networkLoad.entrySet();
    	for (Map.Entry<String, NetworkLoadVO> entry :  entries) {
    		 if (networkHashRead.containsKey(entry.getKey())) {
    			 networkVO1 = entry.getValue();
                 networkVO2 = entry.getValue();
                 networkVO1.setTotalRefusedCount(networkVO2.getTotalRefusedCount());
                 networkVO1.setLastReceievedTime(networkVO2.getLastReceievedTime());
                 networkVO1.setRequestCount(networkVO2.getRequestCount());
                 networkVO1.setLastRefusedTime(networkVO2.getLastRefusedTime());
                 networkVO1.setLastTxnProcessStartTime(networkVO2.getLastTxnProcessStartTime());
                 networkVO1.setRecievedCount(networkVO2.getRecievedCount());
    		 }
    	}
    }

    /**
     * Method modifyInterfaceLoadVO this method is used to modify the
     * InterfaceLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */
    private static void modifyInterfaceLoadVO(Hashtable interfaceLoad, Hashtable interfaceHashRead) {
    	 InterfaceLoadVO interfaceLoadVO1 ;
         InterfaceLoadVO interfaceLoadVO2 ;
    	Set<Map.Entry<String, InterfaceLoadVO >> entries = interfaceLoad.entrySet();
    	for (Map.Entry<String, InterfaceLoadVO> entry :  entries) {
    		 if (interfaceHashRead.containsKey(entry.getKey())) {
    			 interfaceLoadVO1 = entry.getValue();
                 interfaceLoadVO2 = entry.getValue();
                 interfaceLoadVO1.setRecievedCount(interfaceLoadVO2.getRecievedCount());
                 interfaceLoadVO1.setRequestCount(interfaceLoadVO2.getRequestCount());
                 interfaceLoadVO1.setTotalRefusedCount(interfaceLoadVO2.getTotalRefusedCount());
                 interfaceLoadVO1.setLastReceievedTime(interfaceLoadVO2.getLastReceievedTime());
                 interfaceLoadVO1.setLastRefusedTime(interfaceLoadVO2.getLastRefusedTime());
                 interfaceLoadVO1.setLastTxnProcessStartTime(interfaceLoadVO2.getLastTxnProcessStartTime());
                 interfaceLoadVO1.setLastQueueAdditionTime(interfaceLoadVO2.getLastQueueAdditionTime());
    		 }
    		
    		
    	}
    }

    /**
     * Method modifyTransactionLoadVO this method is used to modify the
     * TransactionLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */
    private static void modifyTransactionLoadVO(Hashtable transactionLoadVO, Hashtable transactionHashRead) {

    	TransactionLoadVO transactionLoadVO1 ;
        TransactionLoadVO transactionLoadVO2 ;
        Set<Map.Entry<String, TransactionLoadVO >> entries = transactionLoadVO.entrySet();
    	for (Map.Entry<String, TransactionLoadVO> entry :  entries) {
    		  if (transactionHashRead.containsKey(entry.getKey())) {
    			  transactionLoadVO1 = entry.getValue();
                  transactionLoadVO2 = entry.getValue();
                  transactionLoadVO1.setRecievedCount(transactionLoadVO2.getRecievedCount());
                  transactionLoadVO1.setRequestCount(transactionLoadVO2.getRequestCount());
                  transactionLoadVO1.setTotalRefusedCount(transactionLoadVO2.getTotalRefusedCount());
                  transactionLoadVO1.setTotalSenderValidationCount(transactionLoadVO2.getTotalSenderValidationCount());
                  transactionLoadVO1.setTotalRecieverValidationCount(transactionLoadVO2.getTotalRecieverValidationCount());
                  transactionLoadVO1.setTotalSenderTopupCount(transactionLoadVO2.getTotalSenderTopupCount());
                  transactionLoadVO1.setTotalRecieverTopupCount(transactionLoadVO2.getTotalRecieverTopupCount());
                  transactionLoadVO1.setTotalInternalFailCount(transactionLoadVO2.getTotalInternalFailCount());
                  transactionLoadVO1.setTotalSenderValFailCount(transactionLoadVO2.getTotalSenderValFailCount());
                  transactionLoadVO1.setTotalRecieverValFailCount(transactionLoadVO2.getTotalRecieverValFailCount());
                  transactionLoadVO1.setTotalSenderTopupFailCount(transactionLoadVO2.getTotalSenderTopupFailCount());
                  transactionLoadVO1.setTotalRecieverTopupFailCount(transactionLoadVO2.getTotalRecieverTopupFailCount());
                  transactionLoadVO1.setLastReceievedTime(transactionLoadVO2.getLastReceievedTime());
                  transactionLoadVO1.setLastRefusedTime(transactionLoadVO2.getLastRefusedTime());
                  transactionLoadVO1.setLastTxnProcessStartTime(transactionLoadVO2.getLastTxnProcessStartTime());
    		  }
    	}

    }

    /**
     * Method modifyNetworkServiceLoadVO
     * this method is used to modify the NetworkServiceLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */

    private static void modifyNetworkServiceLoadVO(Hashtable networkServiceLoad, Hashtable networkServiceLoadHashRead) {
        
        NetworkServiceLoadVO networkServiceLoadVO1 ;
        NetworkServiceLoadVO networkServiceLoadVO2 ;
        Set<Map.Entry<String, NetworkServiceLoadVO >> entries = networkServiceLoad.entrySet();
    	for (Map.Entry<String, NetworkServiceLoadVO> entry :  entries) {
    		  if (networkServiceLoadHashRead.containsKey(entry.getKey())) {
    			  networkServiceLoadVO1 = entry.getValue();
                  networkServiceLoadVO2 = entry.getValue();
                  networkServiceLoadVO1.setServiceType(networkServiceLoadVO2.getServiceType());
                  networkServiceLoadVO1.setGatewayType(networkServiceLoadVO2.getGatewayType());
                  networkServiceLoadVO1.setServiceName(networkServiceLoadVO2.getServiceName());
                  networkServiceLoadVO1.setRecievedCount(networkServiceLoadVO2.getRecievedCount());
                  networkServiceLoadVO1.setSuccessCount(networkServiceLoadVO2.getSuccessCount());
                  networkServiceLoadVO1.setFailCount(networkServiceLoadVO2.getFailCount());
                  networkServiceLoadVO1.setUnderProcessCount(networkServiceLoadVO2.getUnderProcessCount());
                  networkServiceLoadVO1.setOthersFailCount(networkServiceLoadVO2.getOthersFailCount());
                  networkServiceLoadVO1.setOtherNetworkReqCount(networkServiceLoadVO2.getOtherNetworkReqCount());
                  networkServiceLoadVO1.setBeforeGatewayFoundError(networkServiceLoadVO2.getBeforeGatewayFoundError());
                  networkServiceLoadVO1.setBeforeNetworkFoundError(networkServiceLoadVO2.getBeforeNetworkFoundError());
                  networkServiceLoadVO1.setBeforeServiceTypeFoundError(networkServiceLoadVO2.getBeforeServiceTypeFoundError());
                  networkServiceLoadVO1.setLastReceievedTime(networkServiceLoadVO2.getLastReceievedTime());
                  networkServiceLoadVO1.setAverageServiceTime(networkServiceLoadVO2.getAverageServiceTime());
                  networkServiceLoadVO1.setLastRequestServiceTime(networkServiceLoadVO2.getLastRequestServiceTime());
                  networkServiceLoadVO1.setLastRequestID(networkServiceLoadVO2.getLastRequestID());
    		  }
    	}
    }

    /**
     * Method modifyNetworkServiceHourlyLoadVO this method is used to modify the
     * NetworkServiceHourlyLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */

    private static void modifyNetworkServiceHourlyLoadVO(Hashtable networkServiceHourlyLoad, Hashtable networkServiceLoadHourlyHashRead) {
       
        NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO1 ;
        NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO2 ;
        Set<Map.Entry<String, NetworkServiceHourlyLoadVO >> entries = networkServiceHourlyLoad.entrySet();
    	for (Map.Entry<String, NetworkServiceHourlyLoadVO> entry :  entries) {
    		  if (networkServiceLoadHourlyHashRead.containsKey(entry.getKey())) {
    			  networkServiceHourlyLoadVO1 = entry.getValue();
                  networkServiceHourlyLoadVO2 = entry.getValue();
                  networkServiceHourlyLoadVO1.setServiceType(networkServiceHourlyLoadVO2.getServiceType());
                  networkServiceHourlyLoadVO1.setGatewayType(networkServiceHourlyLoadVO2.getGatewayType());
                  networkServiceHourlyLoadVO1.setServiceName(networkServiceHourlyLoadVO2.getServiceName());
                  networkServiceHourlyLoadVO1.setSuccessCount(networkServiceHourlyLoadVO2.getSuccessCount());
                  networkServiceHourlyLoadVO1.setFailCount(networkServiceHourlyLoadVO2.getFailCount());
                  networkServiceHourlyLoadVO1.setLastReceievedTime(networkServiceHourlyLoadVO2.getLastReceievedTime());
                  networkServiceHourlyLoadVO1.setLastRequestID(networkServiceHourlyLoadVO2.getLastRequestID());
    		  }
    	}
    }

    /**
     * Method writeToFile.
     * This method will save the object's latest state into file .
     * 
     * @author nitin.rohilla
     * @return void
     */
    public static void writeToFile() {
        final String methodName = "writeToFile";
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }
        String path = Constants.getProperty("FILE_PATH");
        ObjectOutput out = null;
        FileOutputStream fos = null;
        File pathDir = new File(path);
        if (!pathDir.exists()) {
            if (!pathDir.mkdirs()) {
                _log.error(methodName, "Unable to create Network counters storage directory");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LoadControllerCache", "", "", "", "Unable to create Network counters storage directory");
            }
        }

        // writing object for Request COUNTER
        try {
            Hashtable networkServiceLoadHashWrite = LoadControllerCache.getNetworkServiceLoadHash();
            File file = new File(path + Constants.getProperty("NETWORK_SERVICE_COUNTER_REPORT"));
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(networkServiceLoadHashWrite);
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" File Created file: ");
            	loggerValue.append(file.getAbsolutePath());
                _log.debug(methodName,loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        // writing object for INSTANCE COUNTER
        try {
            Hashtable instanceHashWrite = LoadControllerCache.getInstanceLoadHash();
            File file = new File(path + Constants.getProperty("INSTANCE_COUNTER_REPORT"));
            fos=null;
            out=null;
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(instanceHashWrite);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " File Created file: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        // Writing Object For Transaction Counter
        try {
            Hashtable transactionHashWrite = LoadControllerCache.getTransactionLoadHash();
            File file = new File(path + Constants.getProperty("TRANSACTION_COUNTER_REPORT"));
            fos=null;
            out=null;
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(transactionHashWrite);
            out.close();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " File Created file: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        try {
            Hashtable networkHashWrite = LoadControllerCache.getNetworkLoadHash();
            File file = new File(path + Constants.getProperty("NETWORK_COUNTER_REPORT"));
            fos=null;
            out=null;
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(networkHashWrite);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " File Created file: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        // Writing Object For Interface Counter
        try {
            Hashtable interfaceHashWrite = LoadControllerCache.getInterfaceLoadHash();
            File file = new File(path + Constants.getProperty("INTERFACE_COUNTER_REPORT"));
            fos=null;
            out=null;
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(interfaceHashWrite);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " File Created file: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }

        // writing object for Hourly Request COUNTER
        try {
            Hashtable networkServiceHourlyLoadHashWrite = LoadControllerCache.getNetworkServiceHourlyLoadHash();
            File file = new File(path + Constants.getProperty("NETWORK_SERVICE_HOURLY_COUNTER_REPORT"));
            fos=null;
            out=null;
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(networkServiceHourlyLoadHashWrite);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " File Created file: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }

    }
    
	/**
	 * @return Returns the _transactionIDCounter.
	 */
	public static synchronized long getTransactionIDCounter() {
	    
	    if(_transactionIDCounter >= MAX_COUNTER)
  		{
  			_transactionIDCounter=1;	
  			_bufferTxnIDCounter=1;
  		}
  		else
  		{
  			_transactionIDCounter++;  			 
  		}
	    return _transactionIDCounter;
	}
	/**
	 * @param counter The _transactionIDCounter to set.
	 */
	public static synchronized void setTransactionIDCounter(long counter) 
	{
		_transactionIDCounter = counter;
		if(_transactionIDCounter>=_bufferTxnIDCounter)
		{
		    _bufferTxnIDCounter+=500;
		    if(_bufferTxnIDCounter>=MAX_COUNTER)
		        _bufferTxnIDCounter=MAX_COUNTER;
		    bufferCounterWrite();
		}
	}
	
	/**
	 * 
	 */
	private static void bufferCounterWrite()
	{
	    String path=Constants.getProperty("FILE_PATH");
		File file=new File(path+Constants.getProperty("INSTANCE_ID")+"TRANSACTION_ID");
		
	    try(FileOutputStream fos = new FileOutputStream(file);
				ObjectOutput out = new ObjectOutputStream(fos);)
		{
	        File pathDir= new File(path);
			if(!pathDir.exists())
	        	if(!pathDir.mkdirs()){
	        		_log.error("bufferCounterWrite","Unable to create Network counters storage directory");
	        		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"LoadControllerCache","","","","Unable to create Network counters storage directory");
	        	}
			Hashtable interfaceHashWrite = new Hashtable();
			interfaceHashWrite.put("TRANSACTION_ID",_bufferTxnIDCounter+"");
			
			out.writeObject(interfaceHashWrite);
			if(_log.isDebugEnabled())_log.debug("bufferCounterWrite"," File Created file: "+file.getAbsolutePath());
		}
		catch(Exception e)
		{
			_log.errorTrace("bufferCounterWrite", e);
		}
	}
	/**
	 * For updating LocalInstanceLoadHash
	 * 
	 */
	
public static void updateLocalInstanceLoadHash(Hashtable _instanceLoadHash,HashMap<String, InstanceLoadVO> hashMap) {
	final String methodName = "updateLocalInstanceLoadHash";
	StringBuilder loggerValue= new StringBuilder(); 
    if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered ");
    }
	 InstanceLoadVO instanceLoadVO1 ;
     InstanceLoadVO instanceLoadVO2 ;
 	for (Entry<String, InstanceLoadVO> entry :  hashMap.entrySet()) {
 		 if (_instanceLoadHash.containsKey(entry.getKey())) {
 			  instanceLoadVO1 = entry.getValue();// redis value
 			  instanceLoadVO2 = (InstanceLoadVO) _instanceLoadHash.get(entry.getKey()); // updating the current map with redis cache 
 			  instanceLoadVO2.setInstanceID(instanceLoadVO1.getInstanceID());
              instanceLoadVO2.setInstanceName(instanceLoadVO1.getInstanceName());
              instanceLoadVO2.setCurrentStatus(instanceLoadVO1.getCurrentStatus());
              instanceLoadVO2.setHostAddress(instanceLoadVO1.getHostAddress());
              instanceLoadVO2.setHostPort(instanceLoadVO1.getHostAddress());
              instanceLoadVO2.setDefinedTransactionLoad(instanceLoadVO1.getDefinedTransactionLoad());
              instanceLoadVO2.setTransactionLoad(instanceLoadVO1.getTransactionLoad());
              instanceLoadVO2.setRequestTimeoutSec(instanceLoadVO1.getRequestTimeoutSec());
              instanceLoadVO2.setDefinedTPS(instanceLoadVO1.getDefinedTPS());
              instanceLoadVO2.setDefualtTPS(instanceLoadVO1.getDefualtTPS());
              instanceLoadVO2.setCurrentTPS(instanceLoadVO1.getCurrentTPS());
              instanceLoadVO2.setLoadType(instanceLoadVO1.getLoadType());
              instanceLoadVO2.setModule(instanceLoadVO1.getModule());
              instanceLoadVO2.setIsDR(instanceLoadVO1.getIsDR());
              instanceLoadVO2.setAuthPass(instanceLoadVO1.getAuthPass());
              instanceLoadVO2.setMaxAllowedLoad(instanceLoadVO1.getMaxAllowedLoad());
              instanceLoadVO2.setInstanceLoadStatus(instanceLoadVO1.isInstanceLoadStatus());
 		 }
 	}
 	if (_log.isDebugEnabled()) {
	    _log.debug(methodName, "Exited ");
	}
}
/**
 * For updating NetworkLoadHash
 * 
 */

public static void updateLocalNetworkLoadHash(Hashtable _networkLoadHash,HashMap<String, NetworkLoadVO> hashMap) {
	 final String methodName = "updateLocalNetworkLoadHash";
		StringBuilder loggerValue= new StringBuilder(); 
	    if (_log.isDebugEnabled()) {
	        _log.debug(methodName, "Entered ");
	    }
	NetworkLoadVO networkVO1 ;
    NetworkLoadVO networkVO2 ;
	for (Entry<String, NetworkLoadVO> entry :  hashMap.entrySet()) {
		 if (_networkLoadHash.containsKey(entry.getKey())) {
			 networkVO1 = entry.getValue();// redis value
             networkVO2 = (NetworkLoadVO) _networkLoadHash.get(entry.getKey()); // updating the current map with redis cache 
             networkVO2.setInstanceID(networkVO1.getInstanceID());
             networkVO2.setNetworkCode(networkVO1.getNetworkCode());
             networkVO2.setDefinedTransactionLoad(networkVO1.getDefinedTransactionLoad());
             networkVO2.setTransactionLoad(networkVO1.getTransactionLoad());
             networkVO2.setRequestTimeoutSec(networkVO1.getRequestTimeoutSec());
             networkVO2.setDefinedTPS(networkVO1.getDefinedTPS());
             networkVO2.setDefualtTPS(networkVO1.getDefualtTPS());
             networkVO2.setMinimumProcessTime(networkVO1.getMinimumProcessTime());
             networkVO2.setC2sInstanceID(networkVO1.getC2sInstanceID());
             networkVO2.setCurrentTPS(networkVO1.getCurrentTPS());
             networkVO2.setP2pInstanceID(networkVO1.getP2pInstanceID());
             networkVO2.setLoadType(networkVO1.getLoadType());
		 }
	}
	if (_log.isDebugEnabled()) {
	    _log.debug(methodName, "Exited ");
	}
}	

/**
 * For updating InterfaceLoadHash
 * 
 */
public static void updateInterfaceLoadHash(Hashtable _interfaceLoadHash,HashMap<String, InterfaceLoadVO> hashMap) {
    final String methodName = "updateInterfaceLoadHash";
	StringBuilder loggerValue= new StringBuilder(); 
    if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered ");
    }
        InterfaceLoadVO interfaceLoadVO1 = null;
        InterfaceLoadVO interfaceLoadVO2 = null;
        
    	for (Entry<String, InterfaceLoadVO> entry :  hashMap.entrySet()) {
    		 if (_interfaceLoadHash.containsKey(entry.getKey())) {
    			 interfaceLoadVO1 =  entry.getValue();// redis value
    			 interfaceLoadVO2 = (InterfaceLoadVO) _interfaceLoadHash.get(entry.getKey()); // updating the current map with redis cache 
                 interfaceLoadVO2.setInstanceID(interfaceLoadVO1.getInstanceID());
                 interfaceLoadVO2.setNetworkCode(interfaceLoadVO1.getNetworkCode());
                 interfaceLoadVO2.setInterfaceID(interfaceLoadVO1.getInterfaceID());
                 interfaceLoadVO2.setTransactionLoad(interfaceLoadVO1.getTransactionLoad());
                 interfaceLoadVO2.setRequestTimeoutSec(interfaceLoadVO1.getRequestTimeoutSec());
                 interfaceLoadVO2.setDefualtTPS(interfaceLoadVO1.getDefualtTPS());
                 interfaceLoadVO2.setCurrentTPS(interfaceLoadVO1.getCurrentTPS());
                 interfaceLoadVO2.setQueueSize(interfaceLoadVO1.getQueueSize());
                 interfaceLoadVO2.setQueueTimeOut(interfaceLoadVO1.getQueueTimeOut());
                 interfaceLoadVO2.setNextQueueCheckCaseAfterSec(interfaceLoadVO1.getNextQueueCheckCaseAfterSec());
                 interfaceLoadVO2.setLoadType(_loadType);
    		 }
        }
    	if (_log.isDebugEnabled()) {
    	    _log.debug(methodName, "Exited ");
    	}
     }
/**
 * For updating transactionLoadHash
 * 
 */
public static void updateTransactionLoadHash(Hashtable transactionLoadHash,HashMap<String, TransactionLoadVO> hashMap) {
    final String methodName = "updateTransactionLoadHash";
	StringBuilder loggerValue= new StringBuilder(); 
    if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered ");
    }
       TransactionLoadVO transactionLoadVO1 = null;
       TransactionLoadVO transactionLoadVO2 = null;
    	for (Entry<String, TransactionLoadVO> entry :  hashMap.entrySet()) {
    		 if (transactionLoadHash.containsKey(entry.getKey())) {
    			 transactionLoadVO1 = entry.getValue();// redis value
    			 transactionLoadVO2 = (TransactionLoadVO) transactionLoadHash.get(entry.getKey()); // updating the current map with redis cache 
    			 transactionLoadVO2.setInstanceID(transactionLoadVO1.getInstanceID());
    			 transactionLoadVO2.setNetworkCode(transactionLoadVO1.getNetworkCode());
    			 transactionLoadVO2.setInterfaceID(transactionLoadVO1.getInterfaceID());
    			 transactionLoadVO2.setServiceType(transactionLoadVO1.getServiceType());
    			 transactionLoadVO2.setDefinedTransactionLoad(transactionLoadVO1.getDefinedTransactionLoad());
    			 transactionLoadVO2.setTransactionLoad(transactionLoadVO1.getTransactionLoad());
    			 transactionLoadVO2.setRequestTimeoutSec(transactionLoadVO1.getRequestTimeoutSec());
    			 transactionLoadVO2.setDefinedTPS(transactionLoadVO1.getDefinedTPS());
    			 transactionLoadVO2.setDefualtTPS(transactionLoadVO1.getDefualtTPS());
    			 transactionLoadVO2.setCurrentTPS(transactionLoadVO1.getCurrentTPS());
    			 transactionLoadVO2.setMinimumServiceTime(transactionLoadVO1.getMinimumServiceTime());
    			 transactionLoadVO2.setDefinedOverFlowCount(transactionLoadVO1.getDefinedOverFlowCount());
    			 transactionLoadVO2.setNextCheckTimeOutCaseAfterSec(transactionLoadVO1.getNextCheckTimeOutCaseAfterSec());
    			 transactionLoadVO2.setLoadType(_loadType);
    		 } 
         }
    	if (_log.isDebugEnabled()) {
    	    _log.debug(methodName, "Exited ");
    	}     
}


/**
 * For updating LocalNetworkServiceHourlyCounterHash
 * 
 */

public static void updateLocalNetServiceHourLoadHash(Hashtable networkServiceHourCounter,HashMap<String, NetworkServiceHourlyLoadVO> hashMap) {
final String methodName = "updateLocalNetServiceHourLoadHash";
StringBuilder loggerValue= new StringBuilder(); 
if (_log.isDebugEnabled()) {
    _log.debug(methodName, "Entered ");
}
NetworkServiceHourlyLoadVO networkHourLoadVO1 ;
NetworkServiceHourlyLoadVO networkHourLoadVO2 ;
	for (Entry<String, NetworkServiceHourlyLoadVO> entry :  hashMap.entrySet()) {
		 if (networkServiceHourCounter.containsKey(entry.getKey())) {
			 networkHourLoadVO1 = entry.getValue();// redis value
			 networkHourLoadVO2 = (NetworkServiceHourlyLoadVO) networkServiceHourCounter.get(entry.getKey()); // updating the current map with redis cache 
			 networkHourLoadVO2.setOtherNetworkReqCount(networkHourLoadVO1.getOtherNetworkReqCount());
			 networkHourLoadVO2.setLastRequestID(networkHourLoadVO1.getLastRequestID());
			 networkHourLoadVO2.setSuccessCount(networkHourLoadVO1.getSuccessCount());
			 networkHourLoadVO2.setFailCount(networkHourLoadVO1.getFailCount());
		 }
	}
	if (_log.isDebugEnabled()) {
	    _log.debug(methodName, "Exited ");
	}
}


/**
 * For updating networkServiceLoadhash
 * 
 */

public static void updateLocalNetServiceLoadHash(Hashtable networkServiceLoadhash,HashMap<String, NetworkServiceLoadVO> hashMap) {
final String methodName = "updateLocalNetServiceLoadHash";
StringBuilder loggerValue= new StringBuilder(); 
if (_log.isDebugEnabled()) {
    _log.debug(methodName, "Entered ");
}
NetworkServiceLoadVO networkServiceLoadVO1 ;
NetworkServiceLoadVO networkServiceLoadVO2 ;
	for (Entry<String, NetworkServiceLoadVO> entry :  hashMap.entrySet()) {
		 if (networkServiceLoadhash.containsKey(entry.getKey())) {
			 networkServiceLoadVO1 = entry.getValue();// redis value
			 networkServiceLoadVO2 = (NetworkServiceLoadVO) networkServiceLoadhash.get(entry.getKey()); // updating the current map with redis cache 
			 networkServiceLoadVO2.setUnderProcessCount(networkServiceLoadVO1.getUnderProcessCount() - 1);
			 networkServiceLoadVO2.setLastRequestID(networkServiceLoadVO1.getLastRequestID());
			 networkServiceLoadVO2.setRecievedCount(networkServiceLoadVO1.getRecievedCount());
			 networkServiceLoadVO2.setLastReceievedTime(networkServiceLoadVO1.getLastReceievedTime());
			 networkServiceLoadVO2.setOtherNetworkReqCount(networkServiceLoadVO1.getOtherNetworkReqCount());
			 networkServiceLoadVO2.setSuccessCount(networkServiceLoadVO1.getSuccessCount());
			 networkServiceLoadVO2.setFailCount(networkServiceLoadVO1.getFailCount());
		 }
	}
	if (_log.isDebugEnabled()) {
	    _log.debug(methodName, "Exited ");
	}
}

/**
 * @param key
 * @return
 */
public static InstanceLoadVO getinstanceLoadForNetworkHashFromRedis(String key) {
	String methodName = "getinstanceLoadForNetworkHashFromRedis";
	if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered key: " + key);
    }
    Jedis jedis = null;
	InstanceLoadVO instanceLoadVo = null;
	 try {
		 RedisActivityLog.log("LoadControllerCache->getinstanceLoadForNetworkHashFromRedis->Start");
		 jedis = RedisConnectionPool.getPoolInstance().getResource();
		 String json = jedis.hget(hkeyinsLoadForNetworkHash,key);
		 RedisActivityLog.log("LoadControllerCache->getinstanceLoadForNetworkHashFromRedis->End");
		 
		 if(!BTSLUtil.isNullString(json)){
			 instanceLoadVo = gson.fromJson(json, InstanceLoadVO.class);
		 }
	 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 }catch (Exception e) {
	 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
	 }
	 finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        }
  return instanceLoadVo;
}

/**
 * @param key
 * @return
 */
public static HashMap<String,InstanceLoadVO> getinstanceLoadHashFromRedis(String key) {
	String methodName = "getinstanceLoadHashFromRedis";
	if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered key: " + key);
    }
    HashMap<String,InstanceLoadVO> insMap =new HashMap<String,InstanceLoadVO>();
    Jedis jedis = null;
	 try {
		 RedisActivityLog.log("LoadControllerCache->getinstanceLoadHashFromRedis->Start");
		 jedis = RedisConnectionPool.getPoolInstance().getResource();
		 Map<String,String> json = jedis.hgetAll(key);
		 RedisActivityLog.log("LoadControllerCache->getinstanceLoadHashFromRedis->End");

		 for (Entry<String, String> entry :json.entrySet()) {
			 insMap.put(entry.getKey(), gson.fromJson(entry.getValue(), InstanceLoadVO.class));
			
		}
	 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 }catch (Exception e) {
	 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
	 }
	 finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        }
  return insMap;
}


/**
 * @param key
 * @return
 */
public static HashMap<String,InterfaceLoadVO> getInterfaceLoadHashFromRedis(String key) {
	String methodName = "getInterfaceLoadHashFromRedis";
	if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered key: " + key);
    }
    HashMap<String,InterfaceLoadVO> insMap =new HashMap<String,InterfaceLoadVO>();
    Jedis jedis = null;
	 try {
		 RedisActivityLog.log("LoadControllerCache->getInterfaceLoadHashFromRedis->Start");
		 jedis = RedisConnectionPool.getPoolInstance().getResource();
		 Map<String,String> json = jedis.hgetAll(key);
		 RedisActivityLog.log("LoadControllerCache->getInterfaceLoadHashFromRedis->End");

		 for (Entry<String, String> entry :json.entrySet()) {
			 insMap.put(entry.getKey(), gson.fromJson(entry.getValue(), InterfaceLoadVO.class));
			
		}
	 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 }catch (Exception e) {
	 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
	 }
	 finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        }
  return insMap;
}


/**
 * @param key
 * @return
 */
public static HashMap<String,TransactionLoadVO> getTrfLoadHashFromRedis(String key) {
	String methodName = "getTrfLoadHashFromRedis";
	if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered key: " + key);
    }
    HashMap<String,TransactionLoadVO> insMap =new HashMap<String,TransactionLoadVO>();
    Jedis jedis = null;
	 try {
		 RedisActivityLog.log("LoadControllerCache->getTrfLoadHashFromRedis->Start");
		 jedis = RedisConnectionPool.getPoolInstance().getResource();
		 Map<String,String> json = jedis.hgetAll(key);
		 RedisActivityLog.log("LoadControllerCache->getTrfLoadHashFromRedis->End");

		 for (Entry<String, String> entry :json.entrySet()) {
			 insMap.put(entry.getKey(), gson.fromJson(entry.getValue(), TransactionLoadVO.class));
			
		}
	 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 }catch (Exception e) {
	 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
	 }
	 finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        }
  return insMap;
}


/**
 * @param key
 * @return
 */
public static HashMap<String,NetworkLoadVO> getNetworkLoadHashFromRedis(String key) {
	String methodName = "getNetworkLoadHashFromRedis";
	if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered key: " + key);
    }
    HashMap<String,NetworkLoadVO> insMap =new HashMap<String,NetworkLoadVO>();
    Jedis jedis = null;
	 try {
		 RedisActivityLog.log("LoadControllerCache->getNetworkLoadHashFromRedis->Start");
		 jedis = RedisConnectionPool.getPoolInstance().getResource();
		 Map<String,String> json = jedis.hgetAll(key);
		 RedisActivityLog.log("LoadControllerCache->getNetworkLoadHashFromRedis->End");

		 for (Entry<String, String> entry :json.entrySet()) {
			 insMap.put(entry.getKey(), gson.fromJson(entry.getValue(), NetworkLoadVO.class));
			
		}
	 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 }catch (Exception e) {
	 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
	 }
	 finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        }
  return insMap;
}



/**
 * @param key
 * @return
 */
public static HashMap<String,NetworkServiceLoadVO> getNetworkServiceLoadHashFromRedis(String key) {
	String methodName = "getNetworkServiceLoadHashFromRedis";
	if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered key: " + key);
    }
    HashMap<String,NetworkServiceLoadVO> insMap =new HashMap<String,NetworkServiceLoadVO>();
    Jedis jedis = null;
	 try {
		 RedisActivityLog.log("LoadControllerCache->getNetworkServiceLoadHashFromRedis->Start");
		 jedis = RedisConnectionPool.getPoolInstance().getResource();
		 Map<String,String> json = jedis.hgetAll(key);
		 RedisActivityLog.log("LoadControllerCache->getNetworkServiceLoadHashFromRedis->End");

		 for (Entry<String, String> entry :json.entrySet()) {
			 insMap.put(entry.getKey(), gson.fromJson(entry.getValue(), NetworkServiceLoadVO.class));
			
		}
	 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 }catch (Exception e) {
	 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
	 }
	 finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        }
  return insMap;
}


/**
 * @param key
 * @return
 */
public static HashMap<String,NetworkServiceHourlyLoadVO> getNetworkServiceHourlyLoadHashFromRedis(String key) {
	String methodName = "getNetworkServiceHourlyLoadHashFromRedis";
	if (_log.isDebugEnabled()) {
        _log.debug(methodName, "Entered key: " + key);
    }
    HashMap<String,NetworkServiceHourlyLoadVO> insMap =new HashMap<String,NetworkServiceHourlyLoadVO>();
    Jedis jedis = null;
	 try {
		 RedisActivityLog.log("LoadControllerCache->getNetworkServiceHourlyLoadHashFromRedis->Start");
		 jedis = RedisConnectionPool.getPoolInstance().getResource();
		 Map<String,String> json = jedis.hgetAll(key);
		 RedisActivityLog.log("LoadControllerCache->getNetworkServiceHourlyLoadHashFromRedis->End");

		 for (Entry<String, String> entry :json.entrySet()) {
			 insMap.put(entry.getKey(), gson.fromJson(entry.getValue(), NetworkServiceHourlyLoadVO.class));
			
		}
	 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 }catch (Exception e) {
	 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
	 }
	 finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        }
  return insMap;
}

}
