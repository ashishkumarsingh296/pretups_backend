package com.selftopup.pretups.master.businesslogic;

/*
 * NetworkServicesCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 18/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to cache the Network Service List
 */

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

public class NetworkServicesCache {

    private static Log _log = LogFactory.getLog(NetworkServicesCache.class.getName());
    private static HashMap _networkServiceMap = new HashMap();
    private static NetworkServiceDAO _networkServiceDAO = new NetworkServiceDAO();

    /**
     * Gets the network service list
     * 
     */
    public static void refreshNetworkServicesList() {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkServicesList", "Entered: ");
        try {
            HashMap tempMap = null;
            // String loadType=null;
            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkServicesList", " Before loading:" + _networkServiceMap);
            tempMap = _networkServiceDAO.loadNetworkServicesList();
            compareMaps(_networkServiceMap, tempMap);
            _networkServiceMap = tempMap;
            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkServicesList", " After loading:" + _networkServiceMap.size());
        } catch (Exception e) {
            _log.error("refreshSimProfileList", "Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * To compare Maps
     * 
     * @param p_previousMap
     * @param p_currentMap
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
                NetworkServiceVO prevVO = (NetworkServiceVO) p_previousMap.get(key);
                NetworkServiceVO curVO = (NetworkServiceVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Delete", getKey(prevVO.getModuleCode(), prevVO.getSenderNetwork(), prevVO.getReceiverNetwork(), prevVO.getServiceType()), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null)
                    CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Add", getKey(curVO.getModuleCode(), curVO.getSenderNetwork(), curVO.getReceiverNetwork(), curVO.getServiceType()), curVO.logInfo()));
                else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO))
                        CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Modify", getKey(curVO.getModuleCode(), curVO.getSenderNetwork(), curVO.getReceiverNetwork(), curVO.getServiceType()), curVO.differences(prevVO)));
                }
            }

            // Note: this case arises when same number of element added and
            // deleted as well
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    NetworkServiceVO mappingVO = (NetworkServiceVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Add", getKey(mappingVO.getModuleCode(), mappingVO.getSenderNetwork(), mappingVO.getReceiverNetwork(), mappingVO.getServiceType()), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }

    /**
     * Get the Network Service Status Info
     * 
     * @param p_moduleCode
     * @param p_senderNetwork
     * @param p_receiverNetwork
     * @param p_serviceType
     * @return NetworkServiceVO
     */
    public static NetworkServiceVO getObject(String p_moduleCode, String p_senderNetwork, String p_receiverNetwork, String p_serviceType) {
        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Entered p_moduleCode " + p_moduleCode + " p_senderNetwork=" + p_senderNetwork + " p_receiverNetwork=" + p_receiverNetwork + " p_serviceType=" + p_serviceType);
        return (NetworkServiceVO) _networkServiceMap.get(getKey(p_moduleCode, p_senderNetwork, p_receiverNetwork, p_serviceType));
    }

    /**
     * Gets the key to be used for map
     * 
     * @param p_moduleCode
     * @param p_senderNetwork
     * @param p_receiverNetwork
     * @param p_serviceType
     * @return String
     */
    public static String getKey(String p_moduleCode, String p_senderNetwork, String p_receiverNetwork, String p_serviceType) {
        return p_moduleCode + "_" + p_senderNetwork + "_" + p_receiverNetwork + "_" + p_serviceType;
    }

}
