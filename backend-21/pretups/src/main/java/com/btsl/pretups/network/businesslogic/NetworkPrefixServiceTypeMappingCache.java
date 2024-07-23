/*
 * Created on Apr 23, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.network.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.util.BTSLUtil;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NetworkPrefixServiceTypeMappingCache {

    private static Log _log = LogFactory.getLog(NetworkPrefixServiceTypeMappingCache.class.getName());

    private static HashMap _prefixServiceMap = new HashMap();
    
    /**
	 * ensures no instantiation
	 */
    private NetworkPrefixServiceTypeMappingCache(){
    	
    }
    /**
     * Load network Prefixes and service mapping on start up
     * 
     */
    public static void loadNWPrefixServiceTypeMappingAtStartup() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadNWPrefixServiceTypeMappingAtStartup()", "Entered");
        }
        _prefixServiceMap = loadMapping();

        if (_log.isDebugEnabled()) {
            _log.debug("loadNWPrefixServiceTypeMappingAtStartup()", "Exited");
        }
    }

    /**
     * To load the MSISDN Prefixes and service type Mapping details
     * 
     * @return
     *         HashMap
     *         NetworkCache
     */
    private static HashMap loadMapping() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadMapping()", "Entered");
        }
        final String METHOD_NAME = "loadMapping";
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap map = null;
        try {
            map = networkDAO.loadNWPrefixServiceTypeMappingCache();
        } catch (Exception e) {
            _log.error("loadMapping()", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadMapping()", "Exited");
        }

        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * NetworkCache
     */
    public static void updateNWPrefixServiceTypeMapping() {
        if (_log.isDebugEnabled()) {
            _log.debug("updateNWPrefixServiceTypeMapping()", " Entered");
        }

        HashMap currentMap = loadMapping();

        if (_prefixServiceMap != null && _prefixServiceMap.size() > 0) {
            compareMaps(_prefixServiceMap, currentMap);
        }

        _prefixServiceMap = currentMap;

        if (_log.isDebugEnabled()) {
            _log.debug("updateNWPrefixServiceTypeMapping()", "Exited " + _prefixServiceMap.size());
        }
    }

    /**
     * Returns the NetworkPrefixServiceTypeVO which have interface ID
     * 
     * @param p_prefixId
     * @param p_interfaceType
     * @param p_action
     * @return NetworkPrefixServiceTypeVO
     */
    public static Object getObject(String p_prefixId, String networkCode) throws BTSLBaseException {
        NetworkPrefixServiceTypeVO mappingVO = null;

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered   p_prefixId: " + p_prefixId);
        }

        String key = p_prefixId + "_" + networkCode;
        mappingVO = (NetworkPrefixServiceTypeVO) _prefixServiceMap.get(key);
        if (mappingVO == null) {
            throw new BTSLBaseException(NetworkPrefixServiceTypeMappingCache.class.getName(), "getObject", PretupsErrorCodesI.NETWORK_PREFIX_SERVICE_MAPPING_NOT_FOUND);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Exited " + mappingVO);
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

        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
        }
        final String METHOD_NAME = "compareMaps";
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
            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                NetworkPrefixServiceTypeVO prevMappingVO = (NetworkPrefixServiceTypeVO) p_previousMap.get(key);
                NetworkPrefixServiceTypeVO curMappingVO = (NetworkPrefixServiceTypeVO) p_currentMap.get(key);

                if (prevMappingVO != null && curMappingVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkPrefixServiceTypeMappingCache", BTSLUtil.formatMessage("Delete", prevMappingVO.getNetworkCode(), prevMappingVO.logInfo()));
                } else if (prevMappingVO == null && curMappingVO != null) {
                    CacheOperationLog.log("NetworkPrefixServiceTypeMappingCache", BTSLUtil.formatMessage("Add", curMappingVO.getNetworkCode(), curMappingVO.logInfo()));
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
                    CacheOperationLog.log("NetworkPrefixServiceTypeMappingCache", BTSLUtil.formatMessage("Add", mappingVO.getNetworkCode(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }

    

    public static void main(String[] args) {
    }
}
