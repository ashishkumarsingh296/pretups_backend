/**
 * @(#)LookupsCache.java
 *                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       <description>
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       avinash.kamthan Mar 13, 2005 Initital Creation
 *                       Ankit Zindal Aug 10, 2006 Modification for
 *                       ID=LKUPCMPMAP
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 * 
 */

package com.btsl.pretups.master.businesslogic;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;*/

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.pretups.master.web.SubLookUpForm;
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
import com.sun.xml.rpc.processor.modeler.j2ee.xml.methodParamPartsMappingType;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author deepa.shyam
 * 
 */
public class SubLookupsCache implements Runnable {

    private static Log _log = LogFactory.getLog(SubLookupsCache.class.getName());
    private static final String CLASS_NAME = "SubLookupsCache";
    private static HashMap<String,ArrayList<SubLookUpVO>> subLookupMap = new HashMap<String,ArrayList<SubLookUpVO>>();
    public static HashMap<String, ArrayList<SubLookUpVO>> getSubLookupMap() {
		return subLookupMap;
	}

	private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeySubLookupMap = "SubLookupsCache";
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,ArrayList<SubLookUpVO>>  sublookupMapCache = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MINUTES)     
    	    .build(new CacheLoader<String,ArrayList<SubLookUpVO>>(){
    			@Override
    			public ArrayList<SubLookUpVO> load(String key) throws Exception {
    				return getSubLookupCacheFromRedis(key);
    			}
    	     });
    
    public void run() {
        try {
            Thread.sleep(50);
            loadSubLookAtStartup();
        } catch (Exception e) {
        	 _log.error("SubLookupsCache init() Exception ", e);
        }
    }
    /**
     * To load the lookup at the server startup
     * 
     * void
     * LookupsCache
     */
    public static void loadSubLookAtStartup() throws BTSLBaseException{
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubLookAtStartup()", "entered");
        }
        String  methodName = "loadSubLookAtStartup";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		Jedis jedis =null;
   	        RedisActivityLog.log("SubLookupsCache->loadSubLookAtStartup->Start");
   			 try {
   				 jedis = RedisConnectionPool.getPoolInstance().getResource();
   				 Pipeline pipeline = jedis.pipelined();
				 if(!jedis.exists(hKeySubLookupMap)) {
					HashMap<String,ArrayList<SubLookUpVO>>  lookUpDetailMap = (HashMap<String,ArrayList<SubLookUpVO>>) loadSubLookups();
					subLookupMap = lookUpDetailMap;
					for (Entry<String,ArrayList<SubLookUpVO>> entry : lookUpDetailMap.entrySet())  {
    					pipeline.hset(hKeySubLookupMap,entry.getKey(), gson.toJson(entry.getValue()));
    					 if (_log.isDebugEnabled()) {
 	   				    	_log.debug(methodName, CLASS_NAME+ " :"+entry.getKey()+" is loaded into Redis Cache");
 		   	             }
    			     } 
					 pipeline.sync();
				 
			   }
   			 }catch(JedisConnectionException je){
					_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   	            throw new BTSLBaseException(SubLookupsCache.class.getName(), methodName,je.getMessage());
			  }catch(NoSuchElementException  ex){
					_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(SubLookupsCache.class.getName(), methodName,ex.getMessage());
			   }catch (Exception e) {
				   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				   _log.errorTrace(methodName, e);
	                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookAtStartup]", "", "", "", "Exception :" + e.getMessage());
				    throw new BTSLBaseException(SubLookupsCache.class.getName(), methodName,e.getMessage());
		      }finally{
   				 if(jedis != null)
   					jedis.close();
   			 }
    	        RedisActivityLog.log("SubLookupsCache->loadSubLookAtStartup->End");
		} else {	 
			  subLookupMap = loadSubLookups();
	   }

        if (_log.isDebugEnabled()) {
            _log.debug("loadLookups()", "exited");
        }
    }

    /**
     * To load the Lookups
     * 
     * @return
     *         HashMap
     *         NetworkCache
     */
    private static HashMap<String,ArrayList<SubLookUpVO>> loadSubLookups() {
        final String methodName = "loadSubLookups";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubLookups()", "entered");
        }

        SubLookUpDAO subLookupsDAO = new SubLookUpDAO();
        HashMap<String,ArrayList<SubLookUpVO>> map = null;
        try {
            map = subLookupsDAO.loadSubLookups();
        } catch (Exception e) {
            _log.error("loadSubLookups() ", e);
            _log.errorTrace(methodName, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubLookups()", "exited");
        }
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * LookupsCache
     * @throws Exception 
     */
    public static void updateData() throws Exception {
    	final String methodName = "updateData";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        HashMap<String,ArrayList<SubLookUpVO>> currentMap = loadSubLookups();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	    	  Jedis jedis = null;
	   	      RedisActivityLog.log("SubLookupsCache->updateData->Start");
	   	      try{
			   jedis = RedisConnectionPool.getPoolInstance().getResource();
			   Pipeline pipeline = jedis.pipelined();
				//Get the byte array for the hash key
			   pipeline.del(hKeySubLookupMap);
				 for (Entry<String,ArrayList<SubLookUpVO>> entry : currentMap.entrySet())  {
			      pipeline.hset(hKeySubLookupMap, entry.getKey(), gson.toJson(entry.getValue()));
				 }
				 pipeline.sync();
			  }catch(JedisConnectionException je){
					_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[updateData]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   	            throw new BTSLBaseException(SubLookupsCache.class.getName(), methodName,je.getMessage());
			  }catch(NoSuchElementException  ex){
					_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[updateData]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(SubLookupsCache.class.getName(), methodName,ex.getMessage());
			   }catch (Exception e) {
				   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				   _log.errorTrace(methodName, e);
	                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[updateData]", "", "", "", "Exception :" + e.getMessage());
				    throw new BTSLBaseException(SubLookupsCache.class.getName(), methodName,e.getMessage());
		      }finally{
				  if(jedis != null)
					  jedis.close();
			  }
	   	     RedisActivityLog.log("SubLookupsCache->updateData->End");
	   	  } else {
	   		  if (subLookupMap != null && subLookupMap.size() > 0) {

	              compareMaps(subLookupMap, currentMap);

	          }

	   		subLookupMap = currentMap;
	       }
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in updating lookups data.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * to load the sub lookup list against lookupType and lookupCode
     * 
     * @param lookupType
     *  @param lookupCode
     * @return ArrayList
     */
    public static ArrayList<SubLookUpVO> getsubLookUpList(String lookupType,String lookupCode) {

        if (_log.isDebugEnabled()) {
            _log.debug("getsubLookUpList()", "entered");
        }
        ArrayList<SubLookUpVO> list = null;
        String methodName ="getsubLookUpList";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
   		 try {
   			list = sublookupMapCache.get(lookupType);
       		 } catch (ExecutionException e) {
    				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
      		        _log.errorTrace(methodName, e);
      	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[getLookupList]", "", "", "", "ExecutionException :" + e.getMessage());
   			}
           	catch (InvalidCacheLoadException e) { 
      		        _log.info(methodName, e);
   			}	
   	      } 
        else {
   		  list = subLookupMap.get(lookupType);
        }
        int i =0;
        while(i<list.size()) {
        	SubLookUpVO subLookUpVO = list.get(i);
        	if(lookupCode.equals(PretupsI.ALL) || lookupCode.equals(subLookUpVO.getLookupCode())){
        		i++;
        	}else{
        		list.remove(i);
        	}
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "exited" + list.size());
        }
        return list;
    }

    /**
     * To load the lookups drop down on the bases of lookUp Type and lookup code
     * @param p_lookupType
     * @paramlookupCode
     * @return
     *         ArrayList
     *         SubLookupsCache
     */
    public static ArrayList<ListValueVO> loadSubLookupDropDown(String lookupType,String lookupCode ) {

        final String methodName = "loadSubLookupDropDown";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "entered  lookupType: " + lookupType + "lookupCode: " + lookupCode);
        }
        ArrayList<ListValueVO> lookupList = new ArrayList<ListValueVO>();
        try {
        	ArrayList<SubLookUpVO> list = null;
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
       		 try {
       			list = sublookupMapCache.get(lookupType);
           		 } catch (ExecutionException e) {
        				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
          		        _log.errorTrace(methodName, e);
          	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookupDropDown]", "", "", "", "ExecutionException :" + e.getMessage());
       			}
               	catch (InvalidCacheLoadException e) { 
          		        _log.info(methodName, e);
       			}	
       	      } 
            else {
            	list = subLookupMap.get(lookupType);
             }
            for (SubLookUpVO subLookUpVO : list) {
            	ListValueVO listValueVO = null;
            	if(lookupCode.equals(PretupsI.ALL) || lookupCode.equals(subLookUpVO.getLookupCode())){
            		listValueVO = new ListValueVO(subLookUpVO.getSubLookupName(), subLookUpVO.getSubLookupCode());
 	                listValueVO.setOtherInfo(subLookUpVO.getLookupType());
 	                listValueVO.setOtherInfo2(subLookUpVO.getLookupCode());
 	                lookupList.add(listValueVO);
	            }
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "exited" + lookupList.size());
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        return lookupList;
    }

    /**
     * 
     * @param lookupType
     * @param lookupCode
     * @return Object
     */
    public static Object getObject(String lookupType, String lookupcode,String subLookupCode) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "entered");
        }
        String methodName = "getObject";
        ArrayList<SubLookUpVO> list = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
     		 try {
     			list = sublookupMapCache.get(lookupType);
         		 } catch (ExecutionException e) {
      				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        		        _log.errorTrace(methodName, e);
        	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[getLookupList]", "", "", "", "ExecutionException :" + e.getMessage());
     			}
             	catch (InvalidCacheLoadException e) { 
        		        _log.info(methodName, e);
     			}	
     	      }else {
     	    	 list = subLookupMap.get(lookupType);
     	      }
        
     
        SubLookUpVO subLookUpVO = null;
        for (int i = 0, k = list.size(); i < k; i++) {
        	subLookUpVO = list.get(i);
            if (subLookupCode != null && lookupcode != null && lookupcode.equalsIgnoreCase(subLookUpVO.getLookupCode()) && subLookupCode.equalsIgnoreCase(subLookUpVO.getSubCode())) {
                break;
            }else {
                subLookUpVO = null;
            }
        }
        if (subLookUpVO == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LookupCache[getObject]", "", "", "", "Exception:Sub Lookup Code not defined Sub Lookup code=" + subLookupCode + " Lookup type:Lookup code=" + lookupType);
            throw new BTSLBaseException("SubLookupCache", "getObject", PretupsErrorCodesI.ERROR_INVALID_LOOKUP_CODE);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "exited" + list.size());
        }

        return subLookUpVO;
    }

    /**
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     * 
     */

    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) throws Exception{

        final String methodName = "compareMaps";
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }
        try {
            Iterator iterator = null;
            if (p_previousMap.size() == p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() > p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() < p_currentMap.size()) {
                iterator = p_currentMap.keySet().iterator();
            }

            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                ArrayList prevLooupList = (ArrayList) p_previousMap.get(key);
                ArrayList curLooupList = (ArrayList) p_currentMap.get(key);

                if (prevLooupList != null && !prevLooupList.isEmpty() && curLooupList != null && curLooupList.isEmpty()) {
                    logData("Deleted", prevLooupList, null);
                } else if (prevLooupList != null && prevLooupList.isEmpty() && curLooupList != null && !curLooupList.isEmpty()) {
                    logData("Added", curLooupList, null);
                } else if ((prevLooupList != null && !prevLooupList.isEmpty()) || (curLooupList != null && !curLooupList.isEmpty())) {
                    logData("Modified", prevLooupList, curLooupList);
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in comparing maps.");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "exited");
        }
    }

    /**
     * 
     * @param p_action
     * @param p_primaryList
     * @param p_secondryList
     *            void
     */
    private static void logData(String p_action, ArrayList p_primaryList, ArrayList p_secondryList) throws Exception {
    	final String methodName = "logData";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, PretupsI.ENTERED +  " p_action: " + p_action + " p_primaryList: " + p_primaryList + " p_secondryList: " + p_secondryList);
        }
        try {
            if (p_secondryList == null) {
                for (int i = 0, k = p_primaryList.size(); i < k; i++) {
                    SubLookUpVO subLookupsVO = (SubLookUpVO) p_primaryList.get(i);
                    // System.out.println(p_action+"   "+lookupsVO.getLookupType()+"   "+lookupsVO.logInfo());
                    CacheOperationLog.log("SubLookupsCache", BTSLUtil.formatMessage(p_action, subLookupsVO.getLookupType(), subLookupsVO.logInfo()));
                }
            } else if (p_primaryList != null)// change is done for
                                             // ID=LKUPCMPMAP. This is done to
                                             // avoid null pointer exception
                                             // when primaryList is null
            // This is done temporarily and will have to be changed in new design
            // of cache.
            {
                ArrayList secondryList = new ArrayList(p_secondryList);
                for (int i = 0, k = p_primaryList.size(); i < k; i++) {
                    SubLookUpVO primaryLookupsVO = (SubLookUpVO) p_primaryList.get(i);
                    boolean flag = false;
                    for (int m = 0; m < secondryList.size(); m++) {
                    	SubLookUpVO secondryLookupsVO = (SubLookUpVO) secondryList.get(m);
                        if (primaryLookupsVO.getLookupType().equals(secondryLookupsVO.getLookupType()) && primaryLookupsVO.getSubCode().equals(secondryLookupsVO.getSubCode())) {
                            if (!primaryLookupsVO.equalssubLookup(secondryLookupsVO)) {
                                CacheOperationLog.log("SubLookupsCache", BTSLUtil.formatMessage(p_action, primaryLookupsVO.getLookupType(), secondryLookupsVO.differences(primaryLookupsVO)));
                            }
                            flag = true;
                            secondryList.remove(m);
                            break;
                        }
                    }

                    if (!flag) {
                        // System.out.println(BTSLUtil.formatMessage("Delete",primaryLookupsVO.getLookupType(),primaryLookupsVO.logInfo()));
                        CacheOperationLog.log("SubLookupsCache", BTSLUtil.formatMessage("Delete", primaryLookupsVO.getLookupType(), primaryLookupsVO.logInfo()));
                    }
                }

                for (int m = 0; m < secondryList.size(); m++) {
                    SubLookUpVO secondryLookupsVO = (SubLookUpVO) secondryList.get(m);
                    // System.out.println(BTSLUtil.formatMessage("Add",secondryLookupsVO.getLookupType(),secondryLookupsVO.logInfo()));
                    CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage("Add", secondryLookupsVO.getLookupType(), secondryLookupsVO.logInfo()));

                }
            }
        }
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in logging data.");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("logData()", " Exited ");
        }
    }

    public static ArrayList<String> getSubLookupCodeList(String p_lookupType,String lookupCode) {
    	 if (_log.isDebugEnabled()) {
             _log.debug("getSubLookupCodeList", " Entered ");
         }
        String methodName = "getSubLookupCodeList";
        ArrayList<String> lookupsCodeList = new ArrayList<String>();
        try {
            ArrayList<SubLookUpVO> list =null; 
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
          		 try {
          			list = sublookupMapCache.get(p_lookupType);
              		 } catch (ExecutionException e) {
           				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
             		        _log.errorTrace(methodName, e);
             	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[getLookupList]", "", "", "", "ExecutionException :" + e.getMessage());
          			}
                  	catch (InvalidCacheLoadException e) { 
             		        _log.info(methodName, e);
          			}	
          	      } 
               else {
          		  list =  subLookupMap.get(p_lookupType);
               }
            for (SubLookUpVO subLookUpVO :list) {
            	if(lookupCode.equals(PretupsI.ALL) || lookupCode.equals(subLookUpVO.getLookupCode()))
            		lookupsCodeList.add(subLookUpVO.getSubLookupCode());
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited" + lookupsCodeList.size());
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        return lookupsCodeList;
    }
    
       /**
     * @param key
     * @return
     */
    public static ArrayList<SubLookUpVO> getSubLookupCacheFromRedis(String key){
        String methodName= "getSubLookupCacheFromRedis";
     	if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Entered key: "+key);
         }
        ArrayList<SubLookUpVO> subLookupList = null;

         Jedis jedis = null;
 		 try {
 	        RedisActivityLog.log("subLookupCache->getSubLookupCacheFromRedis->Start");  
 			jedis = RedisConnectionPool.getPoolInstance().getResource();
 			String json = jedis.hget(hKeySubLookupMap,key);
 			 Type classType = new TypeToken<ArrayList<SubLookUpVO>>() {}.getType();
 			if(json != null)
 				subLookupList=gson.fromJson(json, classType);
        	RedisActivityLog.log("subLookupCache->getSubLookupCacheFromRedis->End");
 		 }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[getNetworkPrefixFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		  }catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[getNetworkPrefixFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   }catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[getNetworkPrefixFromRedis]", "", "", "", "Exception :" + e.getMessage());
	      }finally {
     		 if(jedis != null ) {
     			 jedis.close();
     		 }
     		 if (_log.isDebugEnabled()) {
     	            _log.debug(methodName, "Exited networkPrefixVO: " + subLookupList);
     	        }
     	   }
 		 return subLookupList;
 		 }
}
