/**
 * @(#)TransferRulesCache.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             <description>
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan June 30, 2005 Initital Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.btsl.pretups.transfer.businesslogic;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.pretups.util.PretupsBL;
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

/**
 * @author avinash.kamthan
 * 
 */
public class TransferRulesCache implements Runnable {
    private static Log LOG = LogFactory.getLog(TransferRulesCache.class.getName());

    private static HashMap<String ,TransferRulesVO> _txfrRuleMap = new HashMap<String ,TransferRulesVO>();
    private static HashMap<String ,TransferRulesVO> _prmtrfRuleMap = new HashMap<String ,TransferRulesVO>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyTxfrRuleMap = "TxfrRuleMap";
    private static final String hKeyPrmtrfRuleMap = "PrmtrfRuleMap";
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static LoadingCache<String,HashMap<String,TransferRulesVO>> txfrRuleMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MINUTES)
    	    .build(new CacheLoader<String, HashMap<String,TransferRulesVO>>(){
    			@Override
    			public HashMap<String,TransferRulesVO> load(String key) throws Exception {
    				return getTrnsferRuleObjectFromRedis(key);
    			}
    	     });
    
    private static LoadingCache<String,HashMap<String,TransferRulesVO>> prmTrfRuleMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MINUTES)
    	    .build(new CacheLoader<String, HashMap<String,TransferRulesVO>>(){
    			@Override
    			public HashMap<String,TransferRulesVO> load(String key) throws Exception {
    				return getPrmTrnsferRuleMapFromRedis(key);
    			}
    	     });
    
    public void run() {
		try {
			Thread.sleep(50);
			loadTransferRulesAtStartup();
		} catch (Exception e) {
			LOG.error("loadTransferRulesAtStartup init() Exception ", e);
		}
	}
    
    public static void loadTransferRulesAtStartup() throws BTSLBaseException {
    	final String methodName = "loadTransferRulesAtStartup";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        	
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                 RedisActivityLog.log("TransferRulesCache->loadTransferRulesAtStartup->Start");
                 Jedis jedis = null;
    	   		 try  {
    	   			jedis = RedisConnectionPool.getPoolInstance().getResource();
    					//If key is already present in redis db do not reload
    					if(!jedis.exists(hKeyTxfrRuleMap)) {
    						HashMap<String, TransferRulesVO> _txfrRuleMap1  = loadMapping();
    						jedis.set(hKeyTxfrRuleMap,gson.toJson(_txfrRuleMap1));
    				    }
    					//If key is already present in redis db do not reload
    					if(!jedis.exists(hKeyPrmtrfRuleMap)) {
    						HashMap<String, TransferRulesVO> _prmtrfRuleMap1  = loadPromotionalMapping(); 
    						jedis.set(hKeyPrmtrfRuleMap,gson.toJson(_prmtrfRuleMap1));
    				   }
    	   		 }catch(JedisConnectionException je){
    	   			LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    	   			LOG.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[loadTransferRulesAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   		        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,je.getMessage());
			 }catch(NoSuchElementException  ex){
				 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
				 LOG.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[loadTransferRulesAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,ex.getMessage());
			 }catch (Exception e) {
				 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				 LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[loadTransferRulesAtStartup]", "", "", "", "Exception :" + e.getMessage());
				throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,e.getMessage());
			 }finally {
  	        	if (jedis != null) {
  	        	jedis.close();
  	        	}
  	        }
    	  RedisActivityLog.log("TransferRulesCache->loadTransferRulesAtStartup->End");
        	 } else {
		        _txfrRuleMap = loadMapping();
		        _prmtrfRuleMap = loadPromotionalMapping();
          }
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

    }

    /**
     * To load the MSISDN Prefixes and Interfaces Mapping details
     * 
     * @return
     *         HashMap
     * @throws Exception 
     */
    private static HashMap<String ,TransferRulesVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}

        final TransferDAO transferDAO = new TransferDAO();
        HashMap<String ,TransferRulesVO> map = null;
        try {
            map = transferDAO.loadTransferRuleCache();

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
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * @throws Exception 
     */
    public static void updateTransferRulesMapping() throws Exception {

    	final String methodName = "updateTransferRulesMapping";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        final HashMap<String ,TransferRulesVO> currentMap = loadMapping();
        final HashMap<String ,TransferRulesVO> currentprmtrfRuleMap = loadPromotionalMapping();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            RedisActivityLog.log("TransferRulesCache->updateTransferRulesMapping->Start");
            Jedis jedis = null;
			 try {
				 jedis= RedisConnectionPool.getPoolInstance().getResource();
				 Pipeline pipeline = jedis.pipelined();
				 pipeline.del(hKeyTxfrRuleMap);
				 pipeline.set(hKeyTxfrRuleMap,gson.toJson(currentMap));
				 pipeline.del(hKeyPrmtrfRuleMap);
				 pipeline.set(hKeyPrmtrfRuleMap,gson.toJson(currentprmtrfRuleMap));
				 pipeline.sync();
			 }catch(JedisConnectionException je){
 	   			LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
 	   			LOG.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[updateTransferRulesMapping]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   		        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,je.getMessage());
			 }catch(NoSuchElementException  ex){
				 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
				 LOG.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[updateTransferRulesMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,ex.getMessage());
			 }catch (Exception e) {
				 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				 LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[updateTransferRulesMapping]", "", "", "", "Exception :" + e.getMessage());
				throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,e.getMessage());
			 }finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
	   RedisActivityLog.log("TransferRulesCache->updateTransferRulesMapping->End");
   	} else {
   	 if (_txfrRuleMap != null && _txfrRuleMap.size() > 0) {
         compareMaps(_txfrRuleMap, currentMap);
     }
     if (_prmtrfRuleMap != null && _prmtrfRuleMap.size() > 0) {
         compareMaps(_prmtrfRuleMap, currentprmtrfRuleMap);
     }

     _txfrRuleMap = currentMap;
     _prmtrfRuleMap = currentprmtrfRuleMap;
   	}
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
        		LOG.debug(methodName, PretupsI.EXITED + _txfrRuleMap.size());
        	}
        }
    }

    /**
     * 
     * @param p_module
     * @param p_networkcode
     * @param p_senderSubscriberType
     * @param p_receiverSubscriberType
     * @param p_senderServiceClassID
     * @param p_receiverServiceClassID
     * @return TransferRulesVO
     */
    public static Object getObject(String p_serviceType, String p_module, String p_networkcode, String p_senderSubscriberType, String p_receiverSubscriberType, String p_senderServiceClassID, String p_receiverServiceClassID, String p_subServiceType, String p_rule_level) {
        TransferRulesVO mappingVO = null;
        String methodName = "getObject";
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()",
                            "Entered  p_serviceType=" + p_serviceType + " p_module: " + p_module + " p_senderSubscriberType: " + p_senderSubscriberType + " receiverSubscriberType " + p_receiverSubscriberType + " p_senderServiceClassID: " + p_senderServiceClassID + " p_receiverServiceClassID: " + p_receiverServiceClassID + " p_subServiceType: " + p_subServiceType + " p_rule_level" + p_rule_level);
        }

        final String key = p_serviceType + "_" + p_module + "_" + p_networkcode + "_" + p_senderSubscriberType + "_" + p_receiverSubscriberType + "_" + p_senderServiceClassID + "_" + p_receiverServiceClassID + "_" + p_subServiceType + "_" + p_rule_level;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {

        	try {
				mappingVO = (TransferRulesVO) txfrRuleMemo.get(hKeyTxfrRuleMap).get(key);
			} catch (ExecutionException e) {
				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				LOG.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
        		LOG.info(methodName,  e.getMessage());	
			}
        
        } else {
			   mappingVO = (TransferRulesVO) _txfrRuleMap.get(key);
		  }

        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Exited " + mappingVO);
        }

        return mappingVO;
    }

    /**
     * overloaded existing method getObject to include Request Gateway Code in
     * Transfer Rule Management
     * 
     * @param p_module
     * @param p_networkcode
     * @param p_senderSubscriberType
     * @param p_receiverSubscriberType
     * @param p_senderServiceClassID
     * @param p_receiverServiceClassID
     * @param p_subServiceType
     * @param p_rule_level
     * @param p_gatewayCode
     * @return TransferRulesVO
     * @author harsh.dixit
     */
    public static Object getObject(String p_serviceType, String p_module, String p_networkcode, String p_senderSubscriberType, String p_receiverSubscriberType, String p_senderServiceClassID, String p_receiverServiceClassID, String p_subServiceType, String p_rule_level, String p_gatewayCode) {
        TransferRulesVO mappingVO = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()",
                            "Entered  p_serviceType=" + p_serviceType + " p_module: " + p_module + " p_senderSubscriberType: " + p_senderSubscriberType + " receiverSubscriberType " + p_receiverSubscriberType + " p_senderServiceClassID: " + p_senderServiceClassID + " p_receiverServiceClassID: " + p_receiverServiceClassID + " p_subServiceType: " + p_subServiceType + " p_rule_level" + p_rule_level + " p_gatewayCode" + p_gatewayCode);
        }
        String methodName = "getObject";
        // String key =
        // p_serviceType+"_"+p_module+"_"+p_networkcode+"_"+p_senderSubscriberType+"_"+p_receiverSubscriberType
        // +"_"+p_senderServiceClassID+"_"+p_receiverServiceClassID+"_"+p_subServiceType+"_"+p_rule_level
        // ;
        final String key = p_serviceType + "_" + p_module + "_" + p_networkcode + "_" + p_senderSubscriberType + "_" + p_receiverSubscriberType + "_" + p_senderServiceClassID + "_" + p_receiverServiceClassID + "_" + p_subServiceType + "_" + p_rule_level + "_" + p_gatewayCode;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {

        	try {
				mappingVO = (TransferRulesVO) txfrRuleMemo.get(hKeyTxfrRuleMap).get(key);
			} catch (ExecutionException e) {
				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				LOG.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
        		LOG.info(methodName,  e.getMessage());	
			}
        
        } else {
			   mappingVO = (TransferRulesVO) _txfrRuleMap.get(key);
		  }
        

        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Exited " + mappingVO);
        }

        return mappingVO;
    }

    /**
     * 
     * @param p_module
     * @param p_networkcode
     * @param p_senderSubscriberType
     * @param p_receiverSubscriberType
     * @param p_senderServiceClassID
     * @param p_receiverServiceClassID
     * @return TransferRulesVO
     */
    public static Object getObject(String p_serviceType, String p_module, String p_networkcode, String p_senderSubscriberType, String p_receiverSubscriberType, String p_senderServiceClassID, String p_receiverServiceClassID, String p_subServiceType) {
        TransferRulesVO mappingVO = null;
        String methodName = "getObject";
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()",
                            "Entered  p_serviceType=" + p_serviceType + " p_module: " + p_module + " p_senderSubscriberType: " + p_senderSubscriberType + " receiverSubscriberType " + p_receiverSubscriberType + " p_senderServiceClassID: " + p_senderServiceClassID + " p_receiverServiceClassID: " + p_receiverServiceClassID + " p_subServiceType: " + p_subServiceType);
        }

        final String key = p_serviceType + "_" + p_module + "_" + p_networkcode + "_" + p_senderSubscriberType + "_" + p_receiverSubscriberType + "_" + p_senderServiceClassID + "_" + p_receiverServiceClassID + "_" + p_subServiceType;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				mappingVO = (TransferRulesVO) txfrRuleMemo.get(hKeyTxfrRuleMap).get(key);
			} catch (ExecutionException e) {
				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				LOG.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
        		LOG.info(methodName,  e.getMessage());	
			}
        } else {
			     mappingVO = (TransferRulesVO) _txfrRuleMap.get(key);
		  }
   

        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Exited " + mappingVO);
        }

        return mappingVO;
    }
    
    /**
     * overloaded existing method getObject to include request gateway,category and grade
     * Transfer Rule Management
     * 
     * @param p_module
     * @param p_networkcode
     * @param p_senderSubscriberType
     * @param p_receiverSubscriberType
     * @param p_senderServiceClassID
     * @param p_receiverServiceClassID
     * @param p_subServiceType
     * @param p_rule_level
     * @param p_gatewayCode
     * * @param p_categoryCode
     * * @param p_gradeCode
     * @return TransferRulesVO
     * @author Ashutosh
     */
    public static Object getObject(String p_serviceType, String p_module, String p_networkcode, String p_senderSubscriberType, String p_receiverSubscriberType, String p_senderServiceClassID, String p_receiverServiceClassID, String p_subServiceType, String p_rule_level, String p_gatewayCode, String p_categoryCode, String p_gradeCode) {
        TransferRulesVO mappingVO = null;
        String  methodName = "getObject";
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()",
                            "Entered  p_serviceType=" + p_serviceType + " p_module: " + p_module + " p_senderSubscriberType: " + p_senderSubscriberType + " receiverSubscriberType " + p_receiverSubscriberType + " p_senderServiceClassID: " + p_senderServiceClassID + " p_receiverServiceClassID: " + p_receiverServiceClassID + " p_subServiceType: " + p_subServiceType + " p_rule_level" + p_rule_level + " p_gatewayCode" + p_gatewayCode+ " p_categoryCode" + p_categoryCode+ " p_gradeCode" + p_gradeCode);
        }

        // String key =
        // p_serviceType+"_"+p_module+"_"+p_networkcode+"_"+p_senderSubscriberType+"_"+p_receiverSubscriberType
        // +"_"+p_senderServiceClassID+"_"+p_receiverServiceClassID+"_"+p_subServiceType+"_"+p_rule_level
        // ;
        final String key = p_serviceType + "_" + p_module + "_" + p_networkcode + "_" + p_senderSubscriberType + "_" + p_receiverSubscriberType + "_" + p_senderServiceClassID + "_" + p_receiverServiceClassID + "_" + p_subServiceType + "_" + p_rule_level + "_" + p_gatewayCode + "_" + p_categoryCode + "_" + p_gradeCode;
       
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				mappingVO = (TransferRulesVO) txfrRuleMemo.get(hKeyTxfrRuleMap).get(key);
			} catch (ExecutionException e) {
				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				LOG.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
        		LOG.info(methodName,  e.getMessage());	
			}
        } else {
			   mappingVO = (TransferRulesVO) _txfrRuleMap.get(key);

		  }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Exited " + mappingVO);
        }

        return mappingVO;
    }

    /**
     * compare two hashmap and check which have changed and log the value which
     * has been changed
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {

        final String methodName = "compareMaps()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
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

            // to check whether any new network added or not but size of
            boolean isNewAdded = false;
            while (iterator != null && iterator.hasNext()) {
                final String key = (String) iterator.next();
                final TransferRulesVO prevTransferRulesVO = (TransferRulesVO) p_previousMap.get(key);
                final TransferRulesVO curTransferRulesVO = (TransferRulesVO) p_currentMap.get(key);

                if (prevTransferRulesVO != null && curTransferRulesVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    isNewAdded = true;
                    CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Delete", prevTransferRulesVO.getKey(), prevTransferRulesVO.logInfo()));
                } else if (prevTransferRulesVO == null && curTransferRulesVO != null) {
                    // new network added
                    CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Add", curTransferRulesVO.getKey(), curTransferRulesVO.logInfo()));
                } else if (prevTransferRulesVO != null && curTransferRulesVO != null) {
                    if (!curTransferRulesVO.equalsTransferRulesVO(prevTransferRulesVO)) {
                        CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Modify", curTransferRulesVO.getKey(), curTransferRulesVO
                                        .differences(prevTransferRulesVO)));

                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                final HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                final Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    // new network added
                    final TransferRulesVO transferRulesVO = (TransferRulesVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Add", transferRulesVO.getKey(), transferRulesVO.logInfo()));
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
    }

    /**
     * @param p_date
     * @return
     *         integrated from PreTUPS Tigo Guatemala code by rahul
     *         this method loads promotional transfer rule from transfer rule
     *         cache(_txfrRuleMap)
     *         this saves DB hit while doing a transaction
     */
    public static HashMap loadPromotionalTransferRuleCache(Date p_date) {
        final String methodName = "loadPromotionalTransferRuleCache";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "entered" + "p_date ::" + p_date);
        }
        TransferRulesVO transverVO = null;
        final HashMap map = new HashMap();
        boolean isExist = false;
        try {
        	  if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		  _prmtrfRuleMap  = prmTrfRuleMemo.get(hKeyPrmtrfRuleMap);
        	  } 
              final Iterator itr = _prmtrfRuleMap.keySet().iterator();
              while (itr.hasNext()) {
                  transverVO = (TransferRulesVO) _prmtrfRuleMap.get(itr.next());
                  if (!BTSLUtil.isNullString(transverVO.getRuleType()) && transverVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                      {
                          // check if promotional transfer rule is in range then
                          // add to map
                          isExist = PretupsBL.isPromotionalRuleExistInRange(transverVO, p_date);
                          if (isExist) {
                              map.put(transverVO.getKey(), transverVO);
                          } else {
                              continue;
                          }
                      }
                  }
              }
           
        }catch (ExecutionException e) {
			LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[loadPromotionalTransferRuleCache]", "", "", "", "ExecutionException :" + e.getMessage());
		}catch (InvalidCacheLoadException e) { 
			LOG.info(methodName,  e.getMessage());	
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "exited map size " + map.size());
        }
        return map;
    }

    /**
     * To load the MSISDN Prefixes and Interfaces Mapping details
     * 
     * @return
     *         HashMap
     *         Gaurav pandey
     */
    private static HashMap loadPromotionalMapping() {
        final String methodName = "loadPromotionalTransferRuleCache";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "entered");
        }

        final TransferDAO transferDAO = new TransferDAO();
        HashMap map = null;
        try {
            map = transferDAO.loadPromotionalTransferRuleCache();

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "exited");
        }
        return map;
    }

    public static HashMap<String,TransferRulesVO> getTrnsferRuleObjectFromRedis(String key) throws BTSLBaseException{
         String methodName = "getTrnsferRuleObjectFromRedis";
        if (LOG.isDebugEnabled()) {
            LOG.debug("getTrnsferRuleObjectFromRedis()",
                            "Entered  key=" + key );
        }
        Jedis jedis = null;
        HashMap<String,TransferRulesVO> currMap = new HashMap<String,TransferRulesVO>();
		try {
            RedisActivityLog.log("TransferRulesCache->getTrnsferRuleObjectFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.get(key);
			Type classType = new TypeToken<HashMap<String,TransferRulesVO>>() {}.getType();
			if(json != null)
				currMap=gson.fromJson(json, classType);
			RedisActivityLog.log("TransferRulesCache->getTrnsferRuleObjectFromRedis->End");
		 }catch(JedisConnectionException je){
			 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			 LOG.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getTrnsferRuleObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
			 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			 LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getTrnsferRuleObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
			 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 LOG.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getTrnsferRuleObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
			 throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited " + currMap);
        }

        return currMap;
    }
    
    public static HashMap<String,TransferRulesVO> getPrmTrnsferRuleMapFromRedis(String key) throws BTSLBaseException{
        String methodName = "getPrmTrnsferRuleMapFromRedis";
       if (LOG.isDebugEnabled()) {
           LOG.debug("getPrmTrnsferRuleMapFromRedis()",
                           "Entered  key=" + key );
       }
       Jedis jedis = null;
       HashMap<String,TransferRulesVO> currMap = new HashMap<String,TransferRulesVO>();
		try {
           RedisActivityLog.log("TransferRulesCache->getPrmTrnsferRuleMapFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.get(key);
			Type classType = new TypeToken<HashMap<String,TransferRulesVO>>() {}.getType();
			if(json != null)
				currMap=gson.fromJson(json, classType);
			RedisActivityLog.log("TransferRulesCache->getPrmTrnsferRuleMapFromRedis->End");
		 }catch(JedisConnectionException je){
			 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			 LOG.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getPrmTrnsferRuleMapFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
			 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			 LOG.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getPrmTrnsferRuleMapFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
			 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferRulesCache[getPrmTrnsferRuleMapFromRedis]", "", "", "", "Exception :" + e.getMessage());
			 throw new BTSLBaseException(TransferRulesCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
       if (LOG.isDebugEnabled()) {
           LOG.debug(methodName, "Exited " + currMap);
       }

       return currMap;
   }
}
