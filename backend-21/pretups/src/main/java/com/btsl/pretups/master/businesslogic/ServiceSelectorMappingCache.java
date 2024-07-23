package com.btsl.pretups.master.businesslogic;

/*
 * ServiceSelectorMappingCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/05/2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Service Type Selector Mapping Cache
 */

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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

public class ServiceSelectorMappingCache implements Runnable {
    private static Log _log = LogFactory.getLog(ServiceSelectorMappingCache.class.getName());
    private static ArrayList<ServiceSelectorMappingVO> _serviceSelectorList = new ArrayList<ServiceSelectorMappingVO>();
    private static HashMap<String,ServiceSelectorMappingVO> _serviceTypeSelectorMasterMap = new HashMap<String,ServiceSelectorMappingVO>();
    private static final String CLASS_NAME = "ServiceSelectorMappingCache";
    private static final String hKeySvcSelMapCache = "serviceTypeSelectorMasterMap";
    private static final String hKeyServiceSelectorlist = "serviceSelectorList";
    private static final String redisEnable=  BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    
    private static LoadingCache<String,ArrayList<ServiceSelectorMappingVO>>  serviceSelectorListCache = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, ArrayList<ServiceSelectorMappingVO>>(){
    			@Override
    			public ArrayList<ServiceSelectorMappingVO> load(String key) throws Exception {
    				return getServiceSelectoListCache(key);
    			}
    	     });
    
    private static LoadingCache<String,HashMap<String,ServiceSelectorMappingVO>>  serviceSelectorMasterMapCache = CacheBuilder.newBuilder()
    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    .build(new CacheLoader<String, HashMap<String,ServiceSelectorMappingVO>>(){
		@Override
		public HashMap<String,ServiceSelectorMappingVO> load(String key) throws Exception {
			return getServiceTypeSelectorMasterMap(key);
		}
     });
    
    
    public void run() {
        try {
            Thread.sleep(50);
            loadServiceSelectorMappingCacheOnStartUp();
        } catch (Exception e) {
        	 _log.error("ServiceSelectorMappingCache init() Exception ", e);
        }
    }

    public static void loadServiceSelectorMappingCacheOnStartUp() throws BTSLBaseException {

    	final String methodName = "loadServiceSelectorMappingCacheOnStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        getServiceSelectorMapping();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading Service Selector Mapping Cache On Startup.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * Load the cache for the Service type and selectors
     * 
     * @return
     * @throws Exception 
     */
    private static void getServiceSelectorMapping() throws BTSLBaseException {
    	final String methodName = "getServiceSelectorMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}

        ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
        try {
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            RedisActivityLog.log("ServiceSelectorMappingCache->getServiceSelectorMapping->Start");
            Jedis jedis = null;
   			 try {
   			    jedis = RedisConnectionPool.getPoolInstance().getResource();
 				Pipeline pipeline = jedis.pipelined();
				ArrayList<ServiceSelectorMappingVO> serviceSelectorList = serviceSelectorMappingDAO.loadServiceSelectorCache();
				pipeline.set(hKeyServiceSelectorlist,gson.toJson(serviceSelectorList));

				HashMap<String, ServiceSelectorMappingVO> serviceTypeSelectorMasterMap = serviceSelectorMappingDAO.loadServiceTypeSelectorMap();
				pipeline.set(hKeySvcSelMapCache, gson.toJson(serviceTypeSelectorMasterMap));
				
				pipeline.sync();
   			} catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME + "[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
   	            throw new BTSLBaseException(CLASS_NAME, methodName,je.getMessage());
   			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME + "[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(CLASS_NAME, methodName,ex.getMessage());
   			}catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
               EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME + "[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
			    throw new BTSLBaseException(CLASS_NAME, methodName,e.getMessage());
   			}finally {
  	        	if (jedis != null) {
  	        	jedis.close();
  	        	}
  	        }
         	RedisActivityLog.log("ServiceSelectorMappingCache->getServiceSelectorMapping->End");
	   	   	} else {
		   	   	_serviceSelectorList = serviceSelectorMappingDAO.loadServiceSelectorCache();
		        _serviceTypeSelectorMasterMap = serviceSelectorMappingDAO.loadServiceTypeSelectorMap();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading Service Selector Mapping Cache.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + " with _serviceSelectorList size=" + _serviceSelectorList.size());
        		
        	}
        }
    }

    /**
     * 
     * @return ArrayList
     *         * @author pankaj.namdev
     */
    public static ArrayList loadSelectorDropDownForTrfRule() {
        final String METHOD_NAME = "loadSelectorDropDownForTrfRule";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorDropDown()", "");
        }
        ArrayList<ListValueVO> lookupList = new ArrayList<ListValueVO>();
        try {
            ArrayList<ServiceSelectorMappingVO>  list = getServiceSelectorList();
           int lists=list.size();
            for (int i = 0; i < lists; i++) {
                ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) list.get(i);
                ListValueVO listValueVO = new ListValueVO(serviceSelectorMappingVO.getSelectorName(), serviceSelectorMappingVO.getSenderSubscriberType() + ":" + serviceSelectorMappingVO.getReceiverSubscriberType() + ":" + serviceSelectorMappingVO.getSelectorCode() + ":" + serviceSelectorMappingVO.getServiceType());
                // listValueVO.setOtherInfo(lookupsVO.getLookupType());
                lookupList.add(listValueVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadLookupDropDown()", "exited" + lookupList.size());
            }
        }catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return lookupList;
    }

    /**
     * @return ArrayList
     * @author shishupal.singh
     */
    public static ArrayList loadSelectorDropDownForCardGroup() {
        final String METHOD_NAME = "loadSelectorDropDownForCardGroup";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorDropDownForCardGroup()", "");
        }
        ArrayList<ListValueVO> lookupList = new ArrayList<ListValueVO>();
        try {
            ListValueVO listVO = null;
            String str = "";
            ServiceSelectorMappingVO vo = null;
            HashMap<String,ServiceSelectorMappingVO>hm = getServiceSelectorMap();
            Iterator itr = hm.keySet().iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                vo = (ServiceSelectorMappingVO) hm.get(str);
                listVO = new ListValueVO(vo.getSelectorName(), vo.getServiceType() + ":" + vo.getSelectorCode());
                lookupList.add(listVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSelectorDropDownForCardGroup()", "exited" + lookupList.size());
            }
        }catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
			_log.error(METHOD_NAME, PretupsI.EXCEPTION + e.getMessage());
        }
        return lookupList;
    }

    /**
     * Method to return the Service Selector List containing the subscriber
     * types
     * 
     * @return
     */
    public static ArrayList<ServiceSelectorMappingVO> getServiceSelectorList() {
    	String methodName = "getServiceSelectorList";
    	try {
          	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
          		_serviceSelectorList = serviceSelectorListCache.get(hKeyServiceSelectorlist);
          	}
         }catch (ExecutionException e) {
  				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
               EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName +"]", "", "", "", "Exception :" + e.getMessage());
  		 }catch(InvalidCacheLoadException  ex){
 			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
		 }catch (Exception e) {
            _log.error("getSelectorListForServiceType()", "Exception : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName +"]", "", "", "", "Exception:" + e.getMessage());
        }
        return _serviceSelectorList;
    }

    /**
     * Method to return the Master Map containing the Service type and Selectors
     * 
     * @return
     */
    public static HashMap<String,ServiceSelectorMappingVO> getServiceSelectorMap() {
    	String methodName = "getServiceSelectorMap";
    	try {
             if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            	 _serviceTypeSelectorMasterMap = serviceSelectorMasterMapCache.get(hKeySvcSelMapCache);
   	   	   	}
         }catch (ExecutionException e) {
   			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		    _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName +"]", "", "", "", "Exception :" + e.getMessage());
   		}catch(InvalidCacheLoadException  ex){
 			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName + "]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
		 }catch (Exception e) {
             _log.errorTrace(methodName, e);
 			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName +"]", "", "", "", "Exception:" + e.getMessage());
         }
        return _serviceTypeSelectorMasterMap;
    }

    /**
     * Method to get the Default Selector For Service Type
     * 
     * @param p_serviceType
     * @return
     */
    public static ServiceSelectorMappingVO getDefaultSelectorForServiceType(String p_serviceType) {
        final String METHOD_NAME = "getDefaultSelectorForServiceType";
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultSelectorForServiceType()", "Entered for p_serviceType=" + p_serviceType);
        }
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        try {
            ArrayList<ServiceSelectorMappingVO> list = getServiceSelectorList();
            int listsize=list.size();
            for (int i = 0; i < listsize; i++) {
                serviceSelectorMappingVO = (ServiceSelectorMappingVO) list.get(i);
                if (serviceSelectorMappingVO.getServiceType().equalsIgnoreCase(p_serviceType) && serviceSelectorMappingVO.isDefaultCode()) {
                    return serviceSelectorMappingVO;
                }
                serviceSelectorMappingVO = null;
            }
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[getDefaultSelectorForServiceType]", "", "", "", "Exception:" + e.getMessage() + " Check the configuration for service selector mapping");
        } finally {
            if (serviceSelectorMappingVO == null) {
                _log.error("getDefaultSelectorForServiceType()", "Check the configuration for service selector mapping, Default Selector Not Defined");
               EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[getDefaultSelectorForServiceType]", "", "", "", "Check the configuration for service selector mapping, Default Selector Not Defined");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultSelectorForServiceType()", "Exiting for p_serviceType=" + p_serviceType + " with serviceSelectorMappingVO=" + serviceSelectorMappingVO);
            }
        }
        return serviceSelectorMappingVO;
    }

    /**
     * Method to get the Selector List for Service Type
     * 
     * @param p_serviceType
     * @return
     */
    public static ArrayList getSelectorListForServiceType(String p_serviceType) {
        final String METHOD_NAME = "getSelectorListForServiceType";
        if (_log.isDebugEnabled()) {
            _log.debug("getSelectorListForServiceType()", "Entered for p_serviceType=" + p_serviceType);
        }
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        ArrayList<ServiceSelectorMappingVO> selectorList = new ArrayList<ServiceSelectorMappingVO>();
        try {
        	ArrayList<ServiceSelectorMappingVO> list = getServiceSelectorList();
            int listsize=list.size();
            for (int i = 0; i <listsize ; i++) {
                serviceSelectorMappingVO = (ServiceSelectorMappingVO) list.get(i);
                if (serviceSelectorMappingVO.getServiceType().equalsIgnoreCase(p_serviceType)) {
                    selectorList.add(serviceSelectorMappingVO);
                }
                serviceSelectorMappingVO = null;
            }
         }catch (Exception e) {
            _log.error("getSelectorListForServiceType()", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[getSelectorListForServiceType]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getSelectorListForServiceType()", "Exiting for p_serviceType=" + p_serviceType + " with selectorList=" + selectorList.size());
            }
        }
        return selectorList;
    }

    /**
     * @return ArrayList
     * @author Sanjeew Kumar
     */
    public static ArrayList loadSelectorDropDownForCardGroupPPEL() {
        final String METHOD_NAME = "loadSelectorDropDownForCardGroupPPEL";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorDropDownForCardGroupPPEL()", "");
        }
        ArrayList<ListValueVO> lookupList = new ArrayList<ListValueVO>();
        try {
            ListValueVO listVO = null;
            String str = "";
            ServiceSelectorMappingVO vo = null;
            HashMap<String,ServiceSelectorMappingVO> hm = getServiceSelectorMap();
            Iterator itr = hm.keySet().iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                vo = (ServiceSelectorMappingVO) hm.get(str);
                listVO = new ListValueVO(vo.getSelectorName(), vo.getServiceType() + ":" + vo.getSelectorCode() + ":" + vo.getAmountStr() + ":" + vo.isModifiedAllowed()+ ":" + vo.getDescription());
                lookupList.add(listVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSelectorDropDownForCardGroupPPEL()", "exited" + lookupList.size());
            }
        }catch (Exception e) {
			_log.error(METHOD_NAME, PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
        }
        return lookupList;
    }

    
    public static ArrayList<ServiceSelectorMappingVO> getServiceSelectoListCache(String key){
        if (_log.isDebugEnabled()) {
            _log.debug("getPrefixIfMapFromRedis()", "Entered p_serviceTypes: "+key);
        }
        String methodName ="getServiceSelectoListCache";
        ArrayList<ServiceSelectorMappingVO> mappingVO = null;
        Jedis jedis = null;
		 try {
	        RedisActivityLog.log("ServiceSelectorMappingCache->getServiceSelectoListCache->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.get(key);
			Type classType = new TypeToken< ArrayList<ServiceSelectorMappingVO>>() {}.getType();
			if(json != null)
			  mappingVO=gson.fromJson(json, classType);
	         RedisActivityLog.log("ServiceSelectorMappingCache->getServiceSelectoListCache->End");
		 }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName +"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName +"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace(methodName, e);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName +"]", "", "", "", "Exception :" + e.getMessage());
		}
    	 finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    	   }
		 return mappingVO;
		 }

    
    
    
    public static HashMap<String,ServiceSelectorMappingVO> getServiceTypeSelectorMasterMap(String key) {
        String methodName ="getServiceTypeSelectorMasterMap";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName,"Entered key: " + key);
        }
        Jedis jedis = null;
		HashMap<String, ServiceSelectorMappingVO> serviceTypeSelectorMasterMap = null;
		 try {
         	RedisActivityLog.log("ServiceSelectorMappingCache->getServiceTypeSelectorMasterMap->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.get(key);
			Type classType = new TypeToken<HashMap<String,ServiceSelectorMappingVO>>() {}.getType();
			if(json != null){
				serviceTypeSelectorMasterMap=gson.fromJson(json, classType);
			}
        	RedisActivityLog.log("ServiceSelectorMappingCache->getServiceTypeSelectorMasterMap->End");
		  } catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME + "[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
   			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME + "[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   			}catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
               EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME + "[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
   			}finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    		 if (_log.isDebugEnabled()) {
    	            _log.debug(methodName,"Exited serviceTypeSelectorMasterMap :" + serviceTypeSelectorMasterMap);
    	        }
    	   }
		 return serviceTypeSelectorMasterMap;
		 }
}