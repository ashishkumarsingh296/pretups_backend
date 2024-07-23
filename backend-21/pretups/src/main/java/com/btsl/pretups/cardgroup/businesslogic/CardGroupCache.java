package com.btsl.pretups.cardgroup.businesslogic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;

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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author 
 *
 */
public class CardGroupCache implements Runnable{
    private static Log log = LogFactory.getLog(CardGroupCache.class.getName());
    private static ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> _cardGroupMap = new ConcurrentHashMap<String, ArrayList<CardGroupDetailsVO>>();
    private static ConcurrentMap<String, ArrayList<CardGroupSetVersionVO>> _cardGroupVerMap = new ConcurrentHashMap<String, ArrayList<CardGroupSetVersionVO>>();
    private static ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> crdGrpRevPrmtdMap = new ConcurrentHashMap<String, ArrayList<CardGroupDetailsVO>>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyCardGroupMap = "CardGroupMap";
    private static final String hKeyCardGroupVerMap = "CardGroupVerMap";
    private static final String hKeyCrdGrpRevPrmtdMap = "CrdGrpRevPrmtdMap";
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String, ArrayList<CardGroupDetailsVO>> cardGroupMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, ArrayList<CardGroupDetailsVO>>(){
    			@Override
    			public ArrayList<CardGroupDetailsVO> load(String key) throws Exception {
    				return getCardGroupDetailsVOListFromRedis(key);
    			}
    	     });
    
    private static LoadingCache<String, ArrayList<CardGroupSetVersionVO>> cardGroupVerMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, ArrayList<CardGroupSetVersionVO>>(){
    			@Override
    			public ArrayList<CardGroupSetVersionVO> load(String key) throws Exception {
    				return getCardGroupSetVersionVOListFromRedis(key);
    			}
    	     });
    
    private static LoadingCache<String, ArrayList<CardGroupDetailsVO>> crdGrpRevPrmtdMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, ArrayList<CardGroupDetailsVO>>(){
    			@Override
    			public ArrayList<CardGroupDetailsVO> load(String key) throws Exception {
    				return getCardGroupGrpRevDetailsListFromRedis(key);
    			}
    	     });
    
    public void run() {
        try {
            Thread.sleep(50);
            loadCardGroupMapAtStartup();
        } catch (Exception e) {
        	 log.error("CardGroupCache init() Exception ", e);
        }
    }
    
    public static void loadCardGroupMapAtStartup() throws BTSLBaseException {
    	final String methodName = "loadCardGroupMapAtStartup";
    	if (log.isDebugEnabled()) {
    		log.debug(methodName, PretupsI.ENTERED);
    	}
    	CardGroupDAO cardGroupDAO = null;
    	try{ 
    		cardGroupDAO = new CardGroupDAO();
    		 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    	    RedisActivityLog.log("CardGroupCache->loadCardGroupMapAtStartup->Start");
    	    Jedis jedis  = null;
   		     try{
   			      jedis  = RedisConnectionPool.getPoolInstance().getResource();
   			      Pipeline pipeline = jedis.pipelined();
    					//If key is already present in redis db do not reload
    					if(!jedis.exists(hKeyCardGroupVerMap)) {
    						ConcurrentMap<String, ArrayList<CardGroupSetVersionVO>> _cardGroupVerMap1  = (new CardGroupSetDAO()).loadCardGroupVersionCache();
    						for (Entry<String, ArrayList<CardGroupSetVersionVO>> entry : _cardGroupVerMap1.entrySet())  {
    							pipeline.hset(hKeyCardGroupVerMap, entry.getKey(), gson.toJson(entry.getValue()));
    					  } 
    						pipeline.sync();
    				  }
    					//If key is already present in redis db do not reload
    					if(!jedis.exists(hKeyCardGroupMap)) {
    						ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> _cardGroupMap1 = cardGroupDAO.loadCardGroupCache();
    						for (Entry<String, ArrayList<CardGroupDetailsVO>> entry : _cardGroupMap1.entrySet())  {
    							pipeline.hset(hKeyCardGroupMap, entry.getKey(),gson.toJson(entry.getValue()));
    					  } 
    						pipeline.sync();
    				   }	
   					//If key is already present in redis db do not reload
   					if(!jedis.exists(hKeyCrdGrpRevPrmtdMap)) {
   						ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> crdGrpRevPrmtdMap1 = cardGroupDAO.loadCrdGrpRevPrmtdCache();
   						for (Entry<String, ArrayList<CardGroupDetailsVO>> entry : crdGrpRevPrmtdMap1.entrySet())  {
   							pipeline.hset(hKeyCrdGrpRevPrmtdMap, entry.getKey(),gson.toJson(entry.getValue()));
   					  } 
   						pipeline.sync();
   				   }
    		  }catch(JedisConnectionException je){
			 		log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
 	   		        log.errorTrace(methodName, je);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[loadCardGroupMapAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
 	   		        throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,je.getMessage());
 			 }catch(NoSuchElementException  ex){
 			 		log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
 	   		        log.errorTrace(methodName, ex);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[loadCardGroupMapAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 	   		        throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,ex.getMessage());
 			 }catch (Exception e) {
 			 	log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
 			 	log.errorTrace(methodName, e);
 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[loadCardGroupMapAtStartup]", "", "", "", "Exception :" + e.getMessage());
 				throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,e.getMessage());
 			 }finally {
   	        	if (jedis != null) {
   	        	jedis.close();
   	        	}
   	        }
    	   		 RedisActivityLog.log("CardGroupCache->loadCardGroupMapAtStartup->End");
    	   } else {
    		 _cardGroupVerMap =  (new CardGroupSetDAO()).loadCardGroupVersionCache();
    	     _cardGroupMap = cardGroupDAO.loadCardGroupCache();
    	     crdGrpRevPrmtdMap = cardGroupDAO.loadCrdGrpRevPrmtdCache();
    	  }
    	}
    	catch(BTSLBaseException be) {
    		log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
    		log.errorTrace(methodName, be);
    		throw be;
    	}
    	catch (Exception e)
    	{
    		log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		log.errorTrace(methodName, e);
    		throw new BTSLBaseException("CardGroupCache", methodName, "Exception in loading Card Group Cache at startup");
    	} finally {
    		if (log.isDebugEnabled()) {
    			log.debug(methodName, PretupsI.EXITED);
    		}
    	}    

    }


    public static CardGroupDetailsVO getCardGroupDetails(String pCardGroupSetID, long pRequestAmount, java.util.Date p_applicableDate) throws BTSLBaseException {
    	final String methodName = "getCardGroupDetails";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered pCardGroupSetID=" + pCardGroupSetID + " pRequestAmount=" + pRequestAmount + " p_applicableDate=" + p_applicableDate);
        }
        
        CardGroupSetVersionVO cardGroupSetVersionVO = null;
        CardGroupSetVersionVO prevCardGroupSetVersionVO = null;
        ArrayList<CardGroupSetVersionVO> cardGroupVersionList = null;
        CardGroupDetailsVO newCardGroupDetailsVO = null;
        
        String latestVersion = null;
        Iterator iter = null;
        ArrayList<CardGroupDetailsVO> cardGroupList = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;

        boolean cardGroupFound = false;
        try {
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 try {
        			 cardGroupVersionList = cardGroupVerMemo.get(pCardGroupSetID);
            		 } catch (ExecutionException e) {
         				log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
           		        log.errorTrace(methodName, e);
           	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetails]", "", "", "", "ExecutionException :" + e.getMessage());
        			}
                	catch (InvalidCacheLoadException e) { 
           		        log.info(methodName, e);
        			}	
        	 } else {
    			   cardGroupVersionList = _cardGroupVerMap.get(pCardGroupSetID);
    		  }
        	 
            if (cardGroupVersionList != null && !cardGroupVersionList.isEmpty()) {
                iter = cardGroupVersionList.iterator();
                while (iter.hasNext()) {
                    cardGroupSetVersionVO = (CardGroupSetVersionVO) iter.next();
                    if (cardGroupSetVersionVO.getApplicableFrom().compareTo(p_applicableDate) <= 0) {
                        if (prevCardGroupSetVersionVO != null && cardGroupSetVersionVO.getApplicableFrom().compareTo(prevCardGroupSetVersionVO.getApplicableFrom()) < 0) {
                            latestVersion = prevCardGroupSetVersionVO.getVersion();
                        } else {
                            latestVersion = cardGroupSetVersionVO.getVersion();
                            prevCardGroupSetVersionVO = cardGroupSetVersionVO;
                        }
                    }
                }
            } else if (cardGroupVersionList == null || cardGroupVersionList.isEmpty() || prevCardGroupSetVersionVO == null) {
                throw new BTSLBaseException("CardGroupCache", methodName, PretupsErrorCodesI.CARD_GROUP_SETVERNOT_ASSOCIATED, 0, new String[] { PretupsBL.getDisplayAmount(pRequestAmount) }, null);
            }

            if (latestVersion != null) {
            	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            		 try {
            			 cardGroupList = cardGroupMemo.get(pCardGroupSetID + "_" + latestVersion);
                		 } catch (ExecutionException e) {
             				log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
               		        log.errorTrace(methodName, e);
               	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetails]", "", "", "", "ExecutionException :" + e.getMessage());
            			}
                    	catch (InvalidCacheLoadException e) { 
               		        log.info(methodName, e);
                    	}	
            	 } else {
                cardGroupList = _cardGroupMap.get(pCardGroupSetID + "_" + latestVersion);
        	 }
                if(cardGroupList != null && !cardGroupList.isEmpty()){
                	iter = cardGroupList.iterator();
                	while (iter.hasNext()) {
                		cardGroupDetailsVO = (CardGroupDetailsVO) iter.next();
                		if (cardGroupDetailsVO.getStartRange() <= pRequestAmount && cardGroupDetailsVO.getEndRange() >= pRequestAmount) {
                			cardGroupFound = true;
                			break;
                		}
                	}
                }
            }
            if (cardGroupDetailsVO == null || !cardGroupFound) {
            	EventHandler.handle(EventIDI.CARD_GROUP_SLAB_NOT_FOUND, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[initialize]", "", "", "","Card Group slab not found/not in range.");
                throw new BTSLBaseException("CardGroupCache", methodName, PretupsErrorCodesI.CARD_GROUP_VALUE_NOT_IN_RANGE, 0, new String[] { PretupsBL.getDisplayAmount(pRequestAmount) }, null);
            }
            if (cardGroupDetailsVO != null) {
            	newCardGroupDetailsVO = new CardGroupDetailsVO();
                BeanUtils.copyProperties(newCardGroupDetailsVO, cardGroupDetailsVO);
                newCardGroupDetailsVO.setVersion(latestVersion);
            }
        } catch (BTSLBaseException be) {
           log.errorTrace(methodName, be);
           throw be;
        } catch (Exception e) {
            log.error(methodName, "SQLException " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("CardGroupCache", methodName, "error.general.processing");
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting. cardGroupDetailsVO=" + newCardGroupDetailsVO.toString());
        }
        return newCardGroupDetailsVO;
    }
    
    public static CardGroupDetailsVO getCardGroupDetails(String pCardGroupSetID, long pRequestAmount, java.util.Date p_applicableDate,
    		String pVoucherType, String pVoucherSegment, String pProductId) throws BTSLBaseException {
    	final String methodName = "getCardGroupDetails";
    	if (log.isDebugEnabled()) {
    		StringBuilder sb = new StringBuilder("Entered pCardGroupSetID=");
    		sb.append(pCardGroupSetID);
    		sb.append(" pRequestAmount=");
    		sb.append(pRequestAmount);
    		sb.append(" p_applicableDate=");
    		sb.append(p_applicableDate);
    		sb.append(" pVoucherType=");
    		sb.append(pVoucherType);
    		sb.append(" pVoucherSegment=");
    		sb.append(pVoucherSegment);
    		sb.append(" pProductId=");
    		sb.append(pProductId);
            log.debug(methodName, sb.toString());
        }
        
        CardGroupSetVersionVO cardGroupSetVersionVO = null;
        CardGroupSetVersionVO prevCardGroupSetVersionVO = null;
        ArrayList<CardGroupSetVersionVO> cardGroupVersionList = null;
        CardGroupDetailsVO newCardGroupDetailsVO = null;
        
        String latestVersion = null;
        Iterator iter = null;
        ArrayList<CardGroupDetailsVO> cardGroupList = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;

        boolean cardGroupFound = false;

        try {
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 try {
        			 cardGroupVersionList = cardGroupVerMemo.get(pCardGroupSetID);
            		 } catch (ExecutionException e) {
         				log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
           		        log.errorTrace(methodName, e);
           	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetails]", "", "", "", "ExecutionException :" + e.getMessage());
        			}
                	catch (InvalidCacheLoadException e) { 
           		        log.info(methodName, e);
                	}	 
        	 } else {
    			   cardGroupVersionList = _cardGroupVerMap.get(pCardGroupSetID);
    		  }
            if (cardGroupVersionList != null && !cardGroupVersionList.isEmpty()) {
                iter = cardGroupVersionList.iterator();
                while (iter.hasNext()) {
                    cardGroupSetVersionVO = (CardGroupSetVersionVO) iter.next();
                    if (cardGroupSetVersionVO.getApplicableFrom().compareTo(p_applicableDate) <= 0) {
                        if (prevCardGroupSetVersionVO != null && cardGroupSetVersionVO.getApplicableFrom().compareTo(prevCardGroupSetVersionVO.getApplicableFrom()) < 0) {
                            latestVersion = prevCardGroupSetVersionVO.getVersion();
                        } else {
                            latestVersion = cardGroupSetVersionVO.getVersion();
                            prevCardGroupSetVersionVO = cardGroupSetVersionVO;
                        }
                    }
                }
            } else if (cardGroupVersionList == null || cardGroupVersionList.isEmpty() || prevCardGroupSetVersionVO == null) {
                throw new BTSLBaseException("CardGroupCache", methodName, PretupsErrorCodesI.CARD_GROUP_SETVERNOT_ASSOCIATED, 0, new String[] { PretupsBL.getDisplayAmount(pRequestAmount) }, null);
            }

            if (latestVersion != null) {
            	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            		 try {
            			 cardGroupList = cardGroupMemo.get(pCardGroupSetID + "_" + latestVersion);
                		 } catch (ExecutionException e) {
             				log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
               		        log.errorTrace(methodName, e);
               	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetails]", "", "", "", "ExecutionException :" + e.getMessage());
            			}
                    	catch (InvalidCacheLoadException e) { 
               		        log.info(methodName, e);
            			}	 
            	 
            	 } else {
                cardGroupList = _cardGroupMap.get(pCardGroupSetID + "_" + latestVersion);
        	 }
                if(cardGroupList != null && !cardGroupList.isEmpty()){
                	iter = cardGroupList.iterator();
                	while (iter.hasNext()) {
                		cardGroupDetailsVO = (CardGroupDetailsVO) iter.next();
                		if (cardGroupDetailsVO.getStartRange() == pRequestAmount && cardGroupDetailsVO.getEndRange() == pRequestAmount && 
                				!BTSLUtil.isNullString(cardGroupDetailsVO.getVoucherSegment()) && cardGroupDetailsVO.getVoucherSegment().equals(pVoucherSegment) &&
                				!BTSLUtil.isNullString(cardGroupDetailsVO.getVoucherType()) && cardGroupDetailsVO.getVoucherType().equals(pVoucherType) &&
                				!BTSLUtil.isNullString(cardGroupDetailsVO.getVoucherProductId()) && cardGroupDetailsVO.getVoucherProductId().equals(pProductId)) {
                			cardGroupFound = true;
                			break;
                		}
                	}
                }
            }
            if (cardGroupDetailsVO == null || !cardGroupFound) {
            	EventHandler.handle(EventIDI.CARD_GROUP_SLAB_NOT_FOUND, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[initialize]", "", "", "","Card Group slab not found/not in range.");
                throw new BTSLBaseException("CardGroupCache", methodName, PretupsErrorCodesI.VOUCHER_CARD_GROUP_VALUE_NOT_IN_RANGE, 0, new String[] { PretupsBL.getDisplayAmount(pRequestAmount) }, null);
            }
            if (cardGroupDetailsVO != null) {
            	newCardGroupDetailsVO = new CardGroupDetailsVO();
                BeanUtils.copyProperties(newCardGroupDetailsVO, cardGroupDetailsVO);
                newCardGroupDetailsVO.setVersion(latestVersion);
            }
        } catch (BTSLBaseException be) {
        	log.errorTrace(methodName, be);
           throw be;
        } catch (Exception e) {
            log.error(methodName, "SQLException " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("CardGroupCache", methodName, "error.general.processing");
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting. cardGroupDetailsVO=" + newCardGroupDetailsVO.toString());
        }
        return newCardGroupDetailsVO;
    }

    public static CardGroupDetailsVO getCardRevPrmttdDetails(String pCardGroupSetID, String pCardGroupId) throws BTSLBaseException {
    	final String methodName = "getCardRevPrmttdDetails";
    	if (log.isDebugEnabled()) {
            log.debug("getCardRevPrmttdDetails", "Entered pCardGroupSetID=" + pCardGroupSetID + " p_cardName=" + pCardGroupId);
        }
       
        CardGroupDetailsVO cardGroupDetailsVO = null;
        
        final String reversalPermittedValue = null;
        ArrayList<CardGroupDetailsVO> cardGroupList = null;
        Iterator iter = null;

        try {
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 try {
        		 cardGroupList = crdGrpRevPrmtdMemo.get(pCardGroupSetID + "_" + pCardGroupId);
        		 } catch (ExecutionException e) {
     				log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
       		        log.errorTrace(methodName, e);
       	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardRevPrmttdDetails]", "", "", "", "ExecutionException :" + e.getMessage());
    			}
            	catch (InvalidCacheLoadException e) { 
       		        log.info(methodName, e);
    			}
    		 } else {
    		cardGroupList = crdGrpRevPrmtdMap.get(pCardGroupSetID + "_" + pCardGroupId);
    	 }
            if (!cardGroupList.isEmpty()) {
                iter = cardGroupList.iterator();
                while (iter.hasNext()) {
                    cardGroupDetailsVO = (CardGroupDetailsVO) iter.next();

                }
            } else {
                throw new BTSLBaseException("CardGroupCache", methodName, PretupsErrorCodesI.ERROR_EXCEPTION, 0, null);
            }

        } catch (BTSLBaseException be) {
        	log.errorTrace(methodName, be);
           throw be;
        } catch (Exception e) {
            log.error(methodName, "SQLException " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardRevPrmttdDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupCache", methodName, "error.general.processing");
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting. cardGroupDetailsVO=" + cardGroupDetailsVO);
        }
        return cardGroupDetailsVO;
    }
    
    public static void updateCardGroupMap() throws BTSLBaseException {
    	final String methodName = "updateCardGroupMap";
    	if (log.isDebugEnabled()) {
    		log.debug(methodName, PretupsI.ENTERED);
    	}
    	CardGroupDAO cardGroupDAO = null;
    	try{ 
    		cardGroupDAO = new CardGroupDAO();
    		 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    	   		 RedisActivityLog.log("CardGroupCache->updateCardGroupMap->Start");
    	   		Jedis jedis = null;
    	   		 try {
    	   			 jedis =   RedisConnectionPool.getPoolInstance().getResource();
    	   			 Pipeline pipeline = jedis.pipelined();
    					//delete the key
    	   			 pipeline.del(hKeyCardGroupVerMap);
    					ConcurrentMap<String, ArrayList<CardGroupSetVersionVO>> _cardGroupVerMap1  = (new CardGroupSetDAO()).loadCardGroupVersionCache();
						for (Entry<String, ArrayList<CardGroupSetVersionVO>> entry : _cardGroupVerMap1.entrySet())  {
							pipeline.hset(hKeyCardGroupVerMap, entry.getKey(), gson.toJson(entry.getValue()));
						} 
						pipeline.sync();
    					//delete the key
						pipeline.del(hKeyCardGroupMap);
						ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> _cardGroupMap1 = cardGroupDAO.loadCardGroupCache();
						for (Entry<String, ArrayList<CardGroupDetailsVO>> entry : _cardGroupMap1.entrySet())  {
							 pipeline.hset(hKeyCardGroupMap, entry.getKey(), gson.toJson(entry.getValue()));
						} 
						pipeline.sync();
   					 	//delete the key
   					     pipeline.del(hKeyCrdGrpRevPrmtdMap);
                              
   						ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> crdGrpRevPrmtdMap1 = cardGroupDAO.loadCrdGrpRevPrmtdCache();
   						for (Entry<String, ArrayList<CardGroupDetailsVO>> entry : crdGrpRevPrmtdMap1.entrySet())  {
   							pipeline.hset(hKeyCrdGrpRevPrmtdMap,entry.getKey(), gson.toJson(entry.getValue()));
   							}
   						pipeline.sync();
    	   		 	}catch(JedisConnectionException je){
    			 		log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
     	   		        log.errorTrace(methodName, je);
     	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[updateCardGroupMap]", "", "", "", "JedisConnectionException :" + je.getMessage());
     	   		        throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,je.getMessage());
     			 }catch(NoSuchElementException  ex){
     			 		log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
     	   		        log.errorTrace(methodName, ex);
     	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[updateCardGroupMap]", "", "", "", "NoSuchElementException :" + ex.getMessage());
     	   		        throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,ex.getMessage());
     			 }catch (Exception e) {
     			 	log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
     			 	log.errorTrace(methodName, e);
     	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[updateCardGroupMap]", "", "", "", "Exception :" + e.getMessage());
     				throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,e.getMessage());
     			 }finally {
       	        	if (jedis != null) {
       	        	jedis.close();
       	        	}
       	        }
    	   		 RedisActivityLog.log("CardGroupCache->updateCardGroupMap->End");
    	   } else {
    		 _cardGroupVerMap =  (new CardGroupSetDAO()).loadCardGroupVersionCache();
    	     _cardGroupMap = cardGroupDAO.loadCardGroupCache();
    	     crdGrpRevPrmtdMap = cardGroupDAO.loadCrdGrpRevPrmtdCache();
    	  }
    	}
    	catch(BTSLBaseException be) {
    		log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
    		log.errorTrace(methodName, be);
    		throw be;
    	}
    	catch (Exception e)
    	{
    		log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		log.errorTrace(methodName, e);
    		throw new BTSLBaseException("CardGroupCache", methodName, "Exception in loading Card Group Cache at update cache");
    	} finally {
    		if (log.isDebugEnabled()) {
    			log.debug(methodName, PretupsI.EXITED);
    		}
    	}    

    }

    public static ArrayList<CardGroupDetailsVO> getCardGroupGrpRevDetailsListFromRedis(String key) throws BTSLBaseException{
    	 RedisActivityLog.log("CardGroupCache->getCardGroupGrpRevDetailsListFromRedis->Start");
    	 Jedis jedis = null;
    	 String methodName ="getCardGroupGrpRevDetailsListFromRedis";
    	 ArrayList<CardGroupDetailsVO> cardGroupList = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String crdGroupGrpRevDetailsListJason = jedis.hget(hKeyCrdGrpRevPrmtdMap, key);
   			 Type classType = new TypeToken< ArrayList<CardGroupDetailsVO>>() {}.getType();
			if(!BTSLUtil.isNullString(crdGroupGrpRevDetailsListJason))
			cardGroupList = gson.fromJson(crdGroupGrpRevDetailsListJason,classType);
		 }catch(JedisConnectionException je){
			 log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			 log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupGrpRevDetailsListFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
			 log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			 log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupGrpRevDetailsListFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
			 log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupGrpRevDetailsListFromRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
   		 RedisActivityLog.log("CardGroupCache->getCardGroupGrpRevDetailsListFromRedis->End");
   		 return cardGroupList;
    }
    
    public static ArrayList<CardGroupSetVersionVO> getCardGroupSetVersionVOListFromRedis(String key) throws BTSLBaseException{
   	 RedisActivityLog.log("CardGroupCache->getCardGroupSetVersionVOListFromRedis->Start");
   	 Jedis jedis = null;
   	 String methodName ="getCardGroupSetVersionVOListFromRedis";
   	 ArrayList<CardGroupSetVersionVO> cardGroupSetVersionList = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String cardGroupSetVersionVOListJason = jedis.hget(hKeyCardGroupVerMap, key);
   			 Type classType = new TypeToken< ArrayList<CardGroupSetVersionVO>>() {}.getType();
			 if(!BTSLUtil.isNullString(cardGroupSetVersionVOListJason))
				cardGroupSetVersionList = gson.fromJson(cardGroupSetVersionVOListJason,classType);
		 }catch(JedisConnectionException je){
			 log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			 log.errorTrace(methodName, je);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupSetVersionVOListFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	         throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
			 log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			 log.errorTrace(methodName, ex);
			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupSetVersionVOListFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
			 log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 log.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupSetVersionVOListFromRedis]", "", "", "", "Exception :" + e.getMessage());
			 throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
  		 RedisActivityLog.log("CardGroupCache->getCardGroupSetVersionVOListFromRedis->End");
  		 return cardGroupSetVersionList;
   }
    public static ArrayList<CardGroupDetailsVO> getCardGroupDetailsVOListFromRedis(String key) throws BTSLBaseException{
      	 RedisActivityLog.log("CardGroupCache->getCardGroupDetailsVOListFromRedis->Start");
      	 Jedis jedis = null;
      	 String methodName ="getCardGroupDetailsVOListFromRedis";
      	 ArrayList<CardGroupDetailsVO> cardGroupDetailsList = null;
   		 try {
   			 jedis = RedisConnectionPool.getPoolInstance().getResource();
   			 Type classType = new TypeToken< ArrayList<CardGroupDetailsVO>>() {}.getType();
   			 String cardGroupDetailsVOListJason = jedis.hget(hKeyCardGroupMap, key);
   			 if(!BTSLUtil.isNullString(cardGroupDetailsVOListJason))
   				cardGroupDetailsList = gson.fromJson(cardGroupDetailsVOListJason,classType);
   		 }catch(JedisConnectionException je){
   			 log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   			 log.errorTrace(methodName, je);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetailsVOListFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	         throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,je.getMessage());
   		 }catch(NoSuchElementException  ex){
   			 log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   			 log.errorTrace(methodName, ex);
   			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetailsVOListFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   			 throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,ex.getMessage());
   		 }catch (Exception e) {
   			 log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   			 log.errorTrace(methodName, e);
   			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetailsVOListFromRedis]", "", "", "", "Exception :" + e.getMessage());
   			 throw new BTSLBaseException(CardGroupCache.class.getName(), methodName,e.getMessage());
   		 }
   		 finally {
   	        	if (jedis != null) {
   	        	jedis.close();
   	        	}
   	        }
     		 RedisActivityLog.log("CardGroupCache->getCardGroupDetailsVOListFromRedis->End");
     		 return cardGroupDetailsList;
      }
    
    
    
}
