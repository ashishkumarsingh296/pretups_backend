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
import java.util.*;
import java.util.Map.Entry;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
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
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author avinash.kamthan
 *
 */
public class LookupsCache implements Runnable {

    private static Log _log = LogFactory.getLog(LookupsCache.class.getName());
    private static final String CLASS_NAME = "LookupsCache";

    private static HashMap _lookupMap = new HashMap();
    private static HashMap _lookupMapRedisBackup = new HashMap();
    public static HashMap get_lookupMap() {
        return _lookupMapRedisBackup;
    }
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeylookupMap = "LookupsCache";
    private static final String  hKeylookupMapCacheClient = "lookupMapCacheClient";
    private static final int keyTimer = 20;//Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    public static void setDummayEntry(){
        //ArrayList<LookupsVO> list = new ArrayList<>() ;

        //  list.add(lookup);

        Set<String> keys = _lookupMap.keySet() ;

        for(String key: keys){
            ArrayList<LookupsVO> lookUpsList = (ArrayList<LookupsVO>) _lookupMap.get(key);

            LookupsVO lookup = new LookupsVO();
            lookup.setLookupCode("String");
            lookup.setLookupName("String");
            lookup.setLookupType("String");
            lookup.setStatus("Y");
            lookUpsList.add(lookup);

            // _lookupMap.put(key, lookUpsList);

            LookupsVO lookup2 = new LookupsVO();
            lookup2.setLookupCode("Y");
            lookup2.setLookupName("Y");
            lookup2.setLookupType("Y");
            lookup2.setStatus("Y");
            lookUpsList.add(lookup2);

            _lookupMap.put(key, lookUpsList);

        }




    }
    private static LoadingCache<String,ArrayList<LookupsVO>>  lookupMapCache = CacheBuilder.newBuilder()
            .expireAfterWrite(keyTimer, TimeUnit.MINUTES)
            .build(new CacheLoader<String,ArrayList<LookupsVO>>(){
                @Override
                public ArrayList<LookupsVO> load(String key) throws Exception {
                    return getLookupCacheFromRedis(key);
                }
            });

    public void run() {
        try {
            Thread.sleep(50);
            loadLookAtStartup();
        } catch (Exception e) {
            _log.error("LookupsCache init() Exception ", e);
        }
    }
    /**
     * To load the lookup at the server startup
     *
     * void
     * LookupsCache
     */
    public static void loadLookAtStartup() throws BTSLBaseException{
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookups()", "entered");
        }
        String  methodName = "loadLookAtStartup";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            Jedis jedis =null;
            RedisActivityLog.log("LookupsCache->loadLookAtStartup->Start");
            try {
                jedis = RedisConnectionPool.getPoolInstance().getResource();
                Pipeline pipeline = jedis.pipelined();
                if(!jedis.exists(hKeylookupMap)) {
                    HashMap<String,ArrayList<LookupsVO>>  lookUpDetailMap = (HashMap<String,ArrayList<LookupsVO>>) loadLookups();
                    _lookupMap = lookUpDetailMap;
                    for (Entry<String,ArrayList<LookupsVO>> entry : lookUpDetailMap.entrySet())  {
                        pipeline.hset(hKeylookupMap,entry.getKey(), gson.toJson(entry.getValue()));
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, CLASS_NAME+ " :"+entry.getKey()+" is loaded into Redis Cache");
                        }
                    }
                    pipeline.sync();
                    if(!jedis.exists(hKeylookupMapCacheClient)){
                        for (Entry<String, ArrayList<LookupsVO>> entry1 : lookUpDetailMap.entrySet())  {
                            if(entry1.getValue() == null) {
                                pipeline.hset(hKeylookupMapCacheClient,entry1.getKey(),"null");
                            }else {
                                pipeline.hset(hKeylookupMapCacheClient,entry1.getKey(),gson.toJson(entry1.getValue()));
                            }

                        }
                        pipeline.sync();
                    }

                }
            }catch(JedisConnectionException je){
                _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
                _log.errorTrace(methodName, je);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
                throw new BTSLBaseException(LookupsCache.class.getName(), methodName,je.getMessage());
            }catch(NoSuchElementException  ex){
                _log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
                throw new BTSLBaseException(LookupsCache.class.getName(), methodName,ex.getMessage());
            }catch (Exception e) {
                _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookAtStartup]", "", "", "", "Exception :" + e.getMessage());
                throw new BTSLBaseException(LookupsCache.class.getName(), methodName,e.getMessage());
            }finally{
                if(jedis != null)
                    jedis.close();
            }
            RedisActivityLog.log("LookupsCache->loadLookAtStartup->End");
        } else {
            _lookupMap = loadLookups();
        }
        //for redis N
        filterCacheData();
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
    private static HashMap<String,ArrayList<LookupsVO>>  loadLookups() {
        final String METHOD_NAME = "loadLookups";
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookups()", "entered");
        }

        LookupsDAO lookupsDAO = new LookupsDAO();
        HashMap<String,ArrayList<LookupsVO>>  map = null;
        try {
            map = lookupsDAO.loadLookups();
        } catch (Exception e) {
            _log.error("loadLookups() ", e);
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookups()", "exited");
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
            HashMap<String,ArrayList<LookupsVO>> currentMap = loadLookups();
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                Jedis jedis = null;
                RedisActivityLog.log("LookupsCache->updateData->Start");
                try{
                    jedis = RedisConnectionPool.getPoolInstance().getResource();
                    Pipeline pipeline = jedis.pipelined();
                    //Get the byte array for the hash key
                    pipeline.del(hKeylookupMap);
                    for (Entry<String,ArrayList<LookupsVO>> entry : currentMap.entrySet())  {
                        pipeline.hset(hKeylookupMap, entry.getKey(), gson.toJson(entry.getValue()));
                    }
                    pipeline.sync();
                    pipeline.del(hKeylookupMapCacheClient);
                    for (Entry<String, ArrayList<LookupsVO>> entry1 : currentMap.entrySet())  {
                        if(entry1.getValue() == null) {
                            pipeline.hset(hKeylookupMapCacheClient,entry1.getKey(),"null");
                        }else {
                            pipeline.hset(hKeylookupMapCacheClient,entry1.getKey(),gson.toJson(entry1.getValue()));
                        }

                    }
                    pipeline.sync();
                }catch(JedisConnectionException je){
                    _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
                    _log.errorTrace(methodName, je);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[updateData]", "", "", "", "JedisConnectionException :" + je.getMessage());
                    throw new BTSLBaseException(LookupsCache.class.getName(), methodName,je.getMessage());
                }catch(NoSuchElementException  ex){
                    _log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
                    _log.errorTrace(methodName, ex);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[updateData]", "", "", "", "NoSuchElementException :" + ex.getMessage());
                    throw new BTSLBaseException(LookupsCache.class.getName(), methodName,ex.getMessage());
                }catch (Exception e) {
                    _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
                    _log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[updateData]", "", "", "", "Exception :" + e.getMessage());
                    throw new BTSLBaseException(LookupsCache.class.getName(), methodName,e.getMessage());
                }finally{
                    if(jedis != null)
                        jedis.close();
                }
                RedisActivityLog.log("LookupsCache->updateData->End");
            } else {
                if (_lookupMap != null && _lookupMap.size() > 0) {

                    compareMaps(_lookupMap, currentMap);

                }

                _lookupMap = currentMap;
            }
            //for redis N
            filterCacheData();
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
     * to load the lookup list against lookupType
     *
     * @param lookupType
     * @return ArrayList
     */
    public static ArrayList getLookupList(String lookupType) {

        if (_log.isDebugEnabled()) {
            _log.debug("getLookupList()", "entered");
        }
        ArrayList list = null;
        String methodName ="getLookupList";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            try {
                list = lookupMapCache.get(lookupType);
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
            list = (ArrayList) _lookupMap.get(lookupType);

        }



        // Collections.sort(list);
        if (_log.isDebugEnabled()) {
            _log.debug("getLookupList()", "exited" + list.size());
        }
        return list;
    }

    /**
     * To load the lookups drop down on the bases of lookUp Type
     * To load the active lookups p_active should be true.
     * if we want to load all lookups then pass p_active should be false
     *
     * @param p_lookupType
     * @param p_active
     * @return
     *         ArrayList
     *         LookupsCache
     */
    public static ArrayList loadLookupDropDown(String p_lookupType, boolean p_active) {

        final String METHOD_NAME = "loadLookupDropDown";
        if (_log.isDebugEnabled()) {
            _log.debug("getLookupList()", "entered  p_lookupType: " + p_lookupType + " p_active: " + p_active);
        }
        ArrayList lookupList = new ArrayList();
        try {
            ArrayList list = null;
            String methodName ="loadLookupDropDown";
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                try {
                    list = lookupMapCache.get(p_lookupType);
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
                list = (ArrayList) _lookupMap.get(p_lookupType);
            }
            for (int i = 0; i < list.size(); i++) {
                LookupsVO lookupsVO = (LookupsVO) list.get(i);

                ListValueVO listValueVO = new ListValueVO(lookupsVO.getLookupName(), lookupsVO.getLookupCode());
                listValueVO.setOtherInfo(lookupsVO.getLookupType());
                /*
                 * Note:
                 * to load only the active lookup if p_status is not "All"
                 */

                /*
                 * if(p_active && "Y".equals(lookupsVO.getStatus()))
                 * lookupList.add(listValueVO);
                 * else
                 */
                lookupList.add(listValueVO);
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadLookupDropDown()", "exited" + lookupList.size());
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return lookupList;
    }

    /**
     *
     * @param lookupType
     * @param lookupCode
     * @return Object
     */
    public static Object getObject(String lookupType, String lookupCode) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "entered");
        }
        String methodName = "getObject";
        ArrayList list = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            try {
                list = lookupMapCache.get(lookupType);
            } catch (ExecutionException e) {
                _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[getLookupList]", "", "", "", "ExecutionException :" + e.getMessage());
            }
            catch (InvalidCacheLoadException e) {
                _log.info(methodName, e);
            }
        }else {
            list = (ArrayList) _lookupMap.get(lookupType);
        }


        LookupsVO lookupsVO = null;

        for (int i = 0, k = list.size(); i < k; i++) {

            lookupsVO = (LookupsVO) list.get(i);
            if (lookupCode != null && lookupCode.equalsIgnoreCase(lookupsVO.getLookupCode())) {
                break;
            } // Method is changed by ankit zindal on date 2/8/06 as logic of
            // searching was wrong
            else {
                lookupsVO = null;
            }
        }
        if (lookupsVO == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LookupCache[getObject]", "", "", "", "Exception:Lookup Code not defined Lookup code=" + lookupCode + " Lookup type=" + lookupType);
            throw new BTSLBaseException("LookupCache", "getObject", PretupsErrorCodesI.ERROR_INVALID_LOOKUP_CODE);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "exited" + list.size());
        }

        return lookupsVO;
    }

    /**
     *
     * @param p_previousMap
     * @param p_currentMap
     *            void
     *
     */

    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) throws Exception{

        final String METHOD_NAME = "compareMaps";
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
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, "Exception in comparing maps.");
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
                    LookupsVO lookupsVO = (LookupsVO) p_primaryList.get(i);
                    // System.out.println(p_action+"   "+lookupsVO.getLookupType()+"   "+lookupsVO.logInfo());
                    CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage(p_action, lookupsVO.getLookupType(), lookupsVO.logInfo()));
                }
            } else if (p_primaryList != null)// change is done for
            // ID=LKUPCMPMAP. This is done to
            // avoid null pointer exception
            // when primaryList is null
            // This is done temorarily and will have to be changed in new design
            // of cache.
            {
                ArrayList secondryList = new ArrayList(p_secondryList);
                for (int i = 0, k = p_primaryList.size(); i < k; i++) {
                    LookupsVO primaryLookupsVO = (LookupsVO) p_primaryList.get(i);
                    boolean flag = false;
                    for (int m = 0; m < secondryList.size(); m++) {
                        LookupsVO secondryLookupsVO = (LookupsVO) secondryList.get(m);
                        if (primaryLookupsVO.getLookupType().equals(secondryLookupsVO.getLookupType()) && primaryLookupsVO.getLookupCode().equals(secondryLookupsVO.getLookupCode())) {
                            if (!primaryLookupsVO.equalsLookup(secondryLookupsVO)) {
                                // /System.out.println(p_action+"   "+primaryLookupsVO.getLookupType()+"   "+secondryLookupsVO.differences(primaryLookupsVO));
                                CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage(p_action, primaryLookupsVO.getLookupType(), secondryLookupsVO.differences(primaryLookupsVO)));
                            }
                            flag = true;
                            secondryList.remove(m);
                            break;
                        }
                    }

                    if (!flag) {
                        // System.out.println(BTSLUtil.formatMessage("Delete",primaryLookupsVO.getLookupType(),primaryLookupsVO.logInfo()));
                        CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage("Delete", primaryLookupsVO.getLookupType(), primaryLookupsVO.logInfo()));
                    }
                }

                for (int m = 0; m < secondryList.size(); m++) {
                    LookupsVO secondryLookupsVO = (LookupsVO) secondryList.get(m);
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

    public static ArrayList<String> getLookupCodeList(String p_lookupType) {
        ArrayList<String> lookupsCodeList = new ArrayList<String>();
        try {
            ArrayList list =null;
            String methodName = "getLookupCodeList";
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                try {
                    list = lookupMapCache.get(p_lookupType);
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
                list = (ArrayList) _lookupMap.get(p_lookupType);
            }
            for (int i = 0; i < list.size(); i++) {
                LookupsVO lookupsVO = (LookupsVO) list.get(i);
                lookupsCodeList.add(lookupsVO.getLookupCode());

            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadLookupDropDown()", "exited" + lookupsCodeList.size());
            }
        } catch (Exception e) {
            _log.errorTrace("getLookupCodeList", e);
        }
        return lookupsCodeList;
    }

    /**
     * To load the lookups drop down on the bases of lookUp Type and p_lookupCode
     * @param p_lookupType
     * @param p_lookupCode
     * @return
     *         ArrayList
     *         LookupsCache
     */
    public static ArrayList loadLookupDropDown(String p_lookupType, String p_lookupCode) {

        final String METHOD_NAME = "loadLookupDropDown";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "entered  p_lookupType: " + p_lookupType + " p_lookupCode: " + p_lookupCode);
        }
        ArrayList lookupList = new ArrayList();
        ArrayList list =null;
        try {

            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                try {
                    list = lookupMapCache.get(p_lookupType);
                } catch (ExecutionException e) {
                    _log.error(METHOD_NAME, PretupsI.EXCEPTION + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LookupsCache[loadLookupDropDown]", "", "", "", "ExecutionException :" + e.getMessage());
                }
                catch (InvalidCacheLoadException e) {
                    _log.info(METHOD_NAME, e);
                }
            }
            else {
                list = (ArrayList) _lookupMap.get(p_lookupType);
            }
            for (int i = 0; i < list.size(); i++) {
                LookupsVO lookupsVO = (LookupsVO) list.get(i);
                if(p_lookupCode.equalsIgnoreCase(lookupsVO.getLookupCode())) {
                    ListValueVO listValueVO = new ListValueVO(lookupsVO.getLookupName(), lookupsVO.getLookupCode());
                    listValueVO.setOtherInfo(lookupsVO.getLookupType());
                    lookupList.add(listValueVO);
                }

            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "exited" + lookupList.size());
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return lookupList;
    }
    public static ArrayList<LookupsVO> getLookupCacheFromRedis(String key){
        String methodName= "getLookupCacheFromRedis";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: "+key);
        }
        ArrayList<LookupsVO> lookupsVOList = null;
        Jedis jedis = null;
        try {
            RedisActivityLog.log("NetworkCache->getLookupCacheFromRedis->Start");
            jedis = RedisConnectionPool.getPoolInstance().getResource();
            String json = jedis.hget(hKeylookupMap,key);
            Type classType = new TypeToken<ArrayList<LookupsVO>>() {}.getType();
            if(json != null)
                lookupsVOList=gson.fromJson(json, classType);
            RedisActivityLog.log("NetworkCache->getLookupCacheFromRedis->End");
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
                _log.debug(methodName, "Exited networkPrefixVO: " + lookupsVOList);
            }
        }
        return lookupsVOList;
    }

    private static void filterCacheData() {
        _lookupMap.forEach((key,value) -> {
            _lookupMapRedisBackup.put(key, gson.toJson(value));
        });
    }

}
