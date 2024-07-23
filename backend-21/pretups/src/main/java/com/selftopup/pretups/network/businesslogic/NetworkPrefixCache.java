/**
 * @(#)NetworkPrefixCache.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             <description>
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan June 21, 2005 Initital Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.selftopup.pretups.network.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class NetworkPrefixCache {

    private static Log _log = LogFactory.getLog(NetworkPrefixCache.class.getName());

    private static HashMap _networkPrefixesMap = new HashMap();

    public static void loadNetworkPrefixesAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkPrefixesAtStartup()", "entered");
        _networkPrefixesMap = loadNetworksPrefixes();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkPrefixesAtStartup()", "exited");
    }

    /**
     * To load the networks details
     * 
     * @return
     *         HashMap
     *         NetworkCache
     */
    private static HashMap loadNetworksPrefixes() {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworksPrefixes()", "entered");
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap map = null;
        try {
            map = networkDAO.loadNetworkPrefixCache();

        } catch (Exception e) {
            _log.error("loadNetworksPrefixes()", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadNetworksPrefixes()", "exited");
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * NetworkPrefixesCache
     */
    public static void updateNetworkPrefixes() {

        if (_log.isDebugEnabled())
            _log.debug("updateNetworkPrefixes()", " Entered");

        HashMap currentMap = loadNetworksPrefixes();

        if (_networkPrefixesMap != null && _networkPrefixesMap.size() > 0) {
            compareMaps(_networkPrefixesMap, currentMap);
        }

        _networkPrefixesMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateNetworkPrefixes()", "exited " + _networkPrefixesMap.size());
    }

    /**
     * to get the requested objcet from the cache
     * 
     * @param p_msisdnPrefix
     * @param p_seriesType
     * @return
     *         Object
     *         NetworkCache
     */
    public static Object getObject(String p_msisdnPrefix, String p_seriesType) {

        NetworkPrefixVO networkPrefixVO = null;

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Entered:msisdnPrefix=" + p_msisdnPrefix + " Series Type" + p_seriesType);

        // if
        // (_log.isDebugEnabled())_log.debug("getObject()","   HashMap="+_networkPrefixesMap);

        networkPrefixVO = (NetworkPrefixVO) _networkPrefixesMap.get(p_msisdnPrefix + "_" + p_seriesType);

        return networkPrefixVO;
    }

    /**
     * 
     * @param p_msisdnPrefix
     * @return NetworkPrefixVO
     */
    public static Object getObject(String p_msisdnPrefix) {

        Object networkPrefixObj = null;
        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Entered:msisdnPrefix=" + p_msisdnPrefix);

        networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_PREPAID);
        if (networkPrefixObj == null) {
            networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_POSTPAID);
            ;
        }

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Exited " + networkPrefixObj);

        return networkPrefixObj;
    }

    /**
     * Method to get the Network Prefix Object of only Operator Based Series
     * 
     * @param p_msisdnPrefix
     * @param p_checkOtherSeries
     *            Pass False: If only Opertaor based series are allowed
     * @return
     */
    public static Object getObject(String p_msisdnPrefix, boolean p_checkOtherSeries) {
        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Entered:msisdnPrefix=" + p_msisdnPrefix + "p_checkOtherSeries=" + p_checkOtherSeries);
        Object networkPrefixObj = null;
        if (p_checkOtherSeries)
            networkPrefixObj = getObject(p_msisdnPrefix);
        else {
            networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_PREPAID);
            if (networkPrefixObj == null || PretupsI.OPERATOR_TYPE_OTH.equals(((NetworkPrefixVO) networkPrefixObj).getOperator())) {
                networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_POSTPAID);
                ;
            }
            if (networkPrefixObj == null || PretupsI.OPERATOR_TYPE_OTH.equals(((NetworkPrefixVO) networkPrefixObj).getOperator())) {
                networkPrefixObj = null;
            }
            if (_log.isDebugEnabled())
                _log.debug("getObject()", "Exited " + networkPrefixObj);
        }
        return networkPrefixObj;
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
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
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
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                NetworkPrefixVO prevNetworkPrefixVO = (NetworkPrefixVO) p_previousMap.get(key);
                NetworkPrefixVO curNetworkPrefixVO = (NetworkPrefixVO) p_currentMap.get(key);

                if (prevNetworkPrefixVO != null && curNetworkPrefixVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    // System.out.println("Network "+prevNetworkVO.getNetworkCode()+" Previous Status  "+prevNetworkVO.getStatus()+" current Status Removed");
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Delete", prevNetworkPrefixVO.getNetworkCode(), prevNetworkPrefixVO.logInfo()));
                } else if (prevNetworkPrefixVO == null && curNetworkPrefixVO != null) {
                    // new network added
                    // System.out.println("Network "+curNetworkVO.getNetworkCode()+"  Added has Status  "+curNetworkVO.getStatus());
                    CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Add", curNetworkPrefixVO.getNetworkCode(), curNetworkPrefixVO.logInfo()));
                } else if (prevNetworkPrefixVO != null && curNetworkPrefixVO != null) {
                    if (!curNetworkPrefixVO.equals(prevNetworkPrefixVO)) {
                        CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Modify", curNetworkPrefixVO.getNetworkCode(), curNetworkPrefixVO.diffrences(prevNetworkPrefixVO)));

                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    // new network added
                    NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Add", networkPrefixVO.getNetworkCode(), networkPrefixVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }
}
