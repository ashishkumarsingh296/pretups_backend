/**
 * @(#)NetworkCache.java
 *                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       <description>
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       avinash.kamthan Mar 10, 2005 Initital Creation
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 * 
 */

package com.selftopup.pretups.network.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.common.ListSorterUtil;
import com.selftopup.common.ListValueVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class NetworkCache {

    private static Log _log = LogFactory.getLog(NetworkCache.class.getName());

    private static HashMap _networkMap = new HashMap();

    public static void loadNetworkAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkAtStartup()", "Entered");
        _networkMap = loadNetworks();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkAtStartup()", "Exited");
    }

    /**
     * To load the networks details
     * 
     * @return
     *         HashMap
     *         NetworkCache
     */
    private static HashMap loadNetworks() {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworks()", "Entered");

        NetworkDAO networkDAO = new NetworkDAO();
        HashMap map = null;
        try {
            map = networkDAO.loadNetworksCache();
        } catch (Exception e) {
            _log.error("loadNetworks()", "Exception e: " + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadNetwork()", "Exited");
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * NetworkCache
     */
    public static void updateNetwork() {

        if (_log.isDebugEnabled())
            _log.debug("updateData()", " Entered");

        HashMap currentMap = loadNetworks();

        if (_networkMap != null && _networkMap.size() > 0) {
            compareMaps(_networkMap, currentMap);
        }

        _networkMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateData()", "Exited " + _networkMap.size());
    }

    /**
     * 
     * @return
     */
    public static ArrayList logDataForWeb() {

        ArrayList _list = new ArrayList();
        if (_log.isDebugEnabled())
            _log.debug("logDataForWeb()", "Entered");

        HashMap currentMap = loadNetworks();
        compareMaps(_networkMap, currentMap);
        if (_log.isDebugEnabled())
            _log.debug("logDataForWeb()", "Exited " + _list.size());
        return _list;
    }

    /**
     * to get the requested objcet from the cache
     * 
     * @param p_networkCode
     * @return
     *         Object
     *         NetworkCache
     */
    public static Object getObject(String p_networkCode) {

        NetworkVO networkVO = null;

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Entered  p_networkCode: " + p_networkCode);

        networkVO = (NetworkVO) _networkMap.get(p_networkCode);

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Exited networkVO: " + networkVO);

        return networkVO;
    }

    /**
     * To load the active networks p_active should be true.
     * if we want to load all network then pass p_active should be false
     * 
     * @param p_active
     *            boolean
     * @return ArrayList
     */
    public static ArrayList loadNetworkDropDown(boolean p_active) {

        if (_log.isDebugEnabled())
            _log.debug("loadNetworkDropDown()", "Entered  " + p_active);

        ArrayList list = new ArrayList();

        Iterator iterator = _networkMap.keySet().iterator();
        NetworkVO networkVO = null;
        while (iterator.hasNext()) {

            networkVO = (NetworkVO) _networkMap.get(iterator.next());

            /*
             * Note:
             * to load only the active network if p_status is not "All"
             */
            if (p_active && "Y".equals(networkVO.getStatus()))
                list.add(new ListValueVO(networkVO.getNetworkName(), networkVO.getNetworkCode()));
            else {
                list.add(new ListValueVO(networkVO.getNetworkName(), networkVO.getNetworkCode()));
            }
        }

        ListSorterUtil listSorterUtil = new ListSorterUtil();
        try {
            list = (ArrayList) listSorterUtil.doSort("label", null, list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (_log.isDebugEnabled())
            _log.debug("loadNetworkDropDown()", "Exited " + list.size());
        return list;
    }

    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }

        compareMaps(p_previousMap, p_currentMap, null);

        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }
    }

    /**
     * compare two hashmap and check which have changed and log the value which
     * has been changed
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap, ArrayList p_list) {

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
                NetworkVO prevNetworkVO = (NetworkVO) p_previousMap.get(key);
                NetworkVO curNetworkVO = (NetworkVO) p_currentMap.get(key);

                if (prevNetworkVO != null && curNetworkVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    // System.out.println(BTSLUtil.formatMessage("Delete",prevNetworkVO.getNetworkCode(),prevNetworkVO.logInfo()));
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_DELETE, prevNetworkVO.getNetworkCode(), prevNetworkVO.logInfo()));
                } else if (prevNetworkVO == null && curNetworkVO != null) {
                    // new network added
                    // System.out.println(BTSLUtil.formatMessage("Add",curNetworkVO.getNetworkCode(),curNetworkVO.logInfo()));
                    CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_ADD, curNetworkVO.getNetworkCode(), curNetworkVO.logInfo()));
                } else if (prevNetworkVO != null && curNetworkVO != null) {
                    if (!curNetworkVO.equals(prevNetworkVO)) {
                        // System.out.println(BTSLUtil.formatMessage("Modify",curNetworkVO.getNetworkCode(),curNetworkVO.differnces(prevNetworkVO)));
                        CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_MODIFY, curNetworkVO.getNetworkCode(), curNetworkVO.differnces(prevNetworkVO)));
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
                NetworkVO networkVO = null;
                while (iterator2.hasNext()) {
                    // new network added
                    networkVO = (NetworkVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_ADD, networkVO.getNetworkCode(), networkVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }

    /**
     * This method returns the HashMap of networkMap
     * 
     * @author ashishk
     */
    public static HashMap getNetworkMap() {
        return _networkMap;
    }

    /**
     * This method returns the networkVO based on the external network code
     * passed.
     * 
     * @param String
     *            p_extNetworkCode - external network code
     * @return Object
     */
    public static Object getNetworkByExtNetworkCode(String p_extNetworkCode) {
        if (_log.isDebugEnabled())
            _log.debug("getNetworkByExtNetworkCode()", "Entered  p_extNetworkCode: " + p_extNetworkCode);
        NetworkVO networkVO = null;
        boolean isFound = false;
        p_extNetworkCode = BTSLUtil.NullToString(p_extNetworkCode).trim();
        Iterator itr = (_networkMap.keySet()).iterator();
        while (itr.hasNext()) {
            networkVO = (NetworkVO) _networkMap.get(itr.next());
            if (p_extNetworkCode.equals(networkVO.getErpNetworkCode())) {
                isFound = true;
                break;
            }
        }
        if (isFound) {
            if (_log.isDebugEnabled())
                _log.debug("getNetworkByExtNetworkCode()", "Exiting....  : networkVO " + networkVO);
            return networkVO;
        }
        if (_log.isDebugEnabled())
            _log.debug("getNetworkByExtNetworkCode()", "Exiting....  : networkVO =null");
        return null;
    }

}
