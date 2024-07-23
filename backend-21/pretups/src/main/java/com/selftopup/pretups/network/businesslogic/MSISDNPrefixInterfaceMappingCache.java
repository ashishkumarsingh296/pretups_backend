/**
 * @(#)MSISDNPrefixInterfaceMappingCache.java
 *                                            Copyright(c) 2005, Bharti Telesoft
 *                                            Ltd.
 *                                            All Rights Reserved
 * 
 *                                            <description>
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 *                                            Author Date History
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 *                                            avinash.kamthan June 22, 2005
 *                                            Initital Creation
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 * 
 */

package com.selftopup.pretups.network.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class MSISDNPrefixInterfaceMappingCache {

    private static Log _log = LogFactory.getLog(MSISDNPrefixInterfaceMappingCache.class.getName());

    private static HashMap _prefixIfMap = new HashMap();

    /**
     * Load the Interface and network refixes mapping on start up
     * 
     */
    public static void loadPrefixInterfaceMappingAtStartup() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadPrefixInterfaceMappingAtStartup()", "Entered");
        }
        _prefixIfMap = loadMapping();

        if (_log.isDebugEnabled())
            _log.debug("loadPrefixInterfaceMappingAtStartup()", "Exited");
    }

    /**
     * To load the MSISDN Prefixes and Interfaces Mapping details
     * 
     * @return
     *         HashMap
     *         NetworkCache
     */
    private static HashMap loadMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadMapping()", "Entered");
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap map = null;
        try {
            map = networkDAO.loadMSISDNInterfaceMappingCache();
        } catch (Exception e) {
            _log.error("loadMapping()", "Exception e:" + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadMapping()", "Exited");

        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * NetworkCache
     */
    public static void updatePrefixInterfaceMapping() {
        if (_log.isDebugEnabled())
            _log.debug("updatePrefixInterfaceMapping()", " Entered");

        HashMap currentMap = loadMapping();

        if (_prefixIfMap != null && _prefixIfMap.size() > 0) {
            compareMaps(_prefixIfMap, currentMap);
        }

        _prefixIfMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updatePrefixInterfaceMapping()", "Exited " + _prefixIfMap.size());
    }

    /**
     * Returns the MSISDNPrefixInterfaceMappingVO which have interface ID
     * 
     * @param p_prefixId
     * @param p_interfaceType
     * @param p_action
     * @return MSISDNPrefixInterfaceMappingVO
     */
    public static Object getObject(long p_prefixId, String p_interfaceType, String p_action) throws BTSLBaseException {
        MSISDNPrefixInterfaceMappingVO mappingVO = null;

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Entered   p_prefixId: " + p_prefixId + " p_interfaceType: " + p_interfaceType + " p_action: " + p_action);

        String key = p_prefixId + "_" + p_interfaceType + "_" + p_action;
        mappingVO = (MSISDNPrefixInterfaceMappingVO) _prefixIfMap.get(key);
        if (mappingVO == null) {
            throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), "getObject", SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND);
        }

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Exited " + mappingVO);
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

        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
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

            boolean isNewAdded = false;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                MSISDNPrefixInterfaceMappingVO prevMappingVO = (MSISDNPrefixInterfaceMappingVO) p_previousMap.get(key);
                MSISDNPrefixInterfaceMappingVO curMappingVO = (MSISDNPrefixInterfaceMappingVO) p_currentMap.get(key);

                if (prevMappingVO != null && curMappingVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("MSISDNPrefixInterfaceMappingCache", BTSLUtil.formatMessage("Delete", prevMappingVO.getNetworkCode(), prevMappingVO.logInfo()));
                } else if (prevMappingVO == null && curMappingVO != null) {
                    CacheOperationLog.log("MSISDNPrefixInterfaceMappingCache", BTSLUtil.formatMessage("Add", curMappingVO.getNetworkCode(), curMappingVO.logInfo()));
                }
            }

            /**
             * Note: this case arises when same number of element added and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    MSISDNPrefixInterfaceMappingVO mappingVO = (MSISDNPrefixInterfaceMappingVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("MSISDNPrefixInterfaceMappingCache", BTSLUtil.formatMessage("Add", mappingVO.getNetworkCode(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }
}
