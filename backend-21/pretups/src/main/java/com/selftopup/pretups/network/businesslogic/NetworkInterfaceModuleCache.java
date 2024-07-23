/**
 * @(#)NetworkInterfaceModuleCache.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      <description>
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      avinash.kamthan June 28, 2005 Initital
 *                                      Creation
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 * 
 */

package com.selftopup.pretups.network.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceDAO;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceVO;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class NetworkInterfaceModuleCache {

    private static Log _log = LogFactory.getLog(NetworkInterfaceModuleCache.class.getName());

    private static HashMap _interfaceModuleMap = new HashMap();
    private static HashMap _interfaceDetailMap = new HashMap();

    /**
     * Load the Network Interface Module on start up
     * 
     */
    public static void loadNetworkInterfaceModuleAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkInterfaceModuleAtStartup()", "Entered");

        _interfaceModuleMap = loadMapping();
        _interfaceDetailMap = loadInterfaceDetail();

        if (_log.isDebugEnabled())
            _log.debug("loadNetworkInterfaceModuleAtStartup()", "Exited");
    }

    /**
     * To load the Network Interface Module details
     * 
     * @return
     *         HashMap
     *         NetworkInterfaceModuleCache
     */
    private static HashMap loadMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadMapping()", "Entered");

        NetworkDAO networkDAO = new NetworkDAO();
        HashMap map = null;
        try {
            map = networkDAO.loadNetworkInterfaceModuleCache();

        } catch (Exception e) {
            _log.error("loadMapping()", "Exception:" + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadMapping()", "Exited MapSize " + map.size());
        }

        return map;
    }

    /**
     * To load the Network Interface Module details
     * 
     * @return
     *         HashMap
     *         NetworkInterfaceModuleCache
     */
    private static HashMap loadInterfaceDetail() {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceDetail()", "Entered");

        InterfaceDAO intDAO = new InterfaceDAO();
        HashMap map = null;
        try {
            map = intDAO.loadInterfaceByID();

        } catch (Exception e) {
            _log.error("loadInterfaceDetail()", "Exception:" + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadInterfaceDetail()", "Exited MapSize " + map.size());
        }

        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * 
     */
    public static void updateNetworkInterfaceModule() {

        if (_log.isDebugEnabled())
            _log.debug("updateNetworkInterfaceModule()", " Entered");

        HashMap currentMap = loadMapping();
        _interfaceDetailMap = loadInterfaceDetail();

        if (_interfaceModuleMap != null && _interfaceModuleMap.size() > 0) {
            compareMaps(_interfaceModuleMap, currentMap);
        }

        _interfaceModuleMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateNetworkInterfaceModule()", "Exited " + _interfaceModuleMap.size());
    }

    /**
     * To get the details information of Network Interface Module
     * 
     * @param p_module
     * @param p_networkCode
     * @param p_methodType
     * @return NetworkInterfaceModuleVO
     */
    public static Object getObject(String p_module, String p_networkCode, String p_methodType) {
        if (_log.isDebugEnabled())
            _log.debug("getObject()", " Entered : p_module: " + p_module + " p_networkCode: " + p_networkCode + " p_methodType: " + p_methodType);

        NetworkInterfaceModuleVO interfaceModuleVO = (NetworkInterfaceModuleVO) _interfaceModuleMap.get(p_module + "_" + p_networkCode + "_" + p_methodType);

        if (_log.isDebugEnabled())
            _log.debug("getObject()", " Exited " + interfaceModuleVO);
        return interfaceModuleVO;
    }

    /**
     * To get the details information of Network Interface Module
     * 
     * @param p_interfaceID
     * @return NetworkInterfaceModuleVO
     */

    public static Object getObject(String p_interfaceID) {
        if (_log.isDebugEnabled())
            _log.debug("getObject()", " Entered : p_interfaceID: " + p_interfaceID);
        InterfaceVO interfaceVO = (InterfaceVO) _interfaceDetailMap.get(p_interfaceID);
        if (_log.isDebugEnabled())
            _log.debug("getObject()", " Exited " + interfaceVO);
        return interfaceVO;
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

            boolean isNewAdded = false;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                NetworkInterfaceModuleVO prevMappingVO = (NetworkInterfaceModuleVO) p_previousMap.get(key);
                NetworkInterfaceModuleVO curMappingVO = (NetworkInterfaceModuleVO) p_currentMap.get(key);

                if (prevMappingVO != null && curMappingVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkInterfaceModuleCache", BTSLUtil.formatMessage("Delete", prevMappingVO.getNetworkCode(), prevMappingVO.logInfo()));
                } else if (prevMappingVO == null && curMappingVO != null) {
                    CacheOperationLog.log("NetworkInterfaceModuleCache", BTSLUtil.formatMessage("Add", curMappingVO.getNetworkCode(), curMappingVO.logInfo()));
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
                    NetworkInterfaceModuleVO mappingVO = (NetworkInterfaceModuleVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkInterfaceModuleCache", BTSLUtil.formatMessage("Add", mappingVO.getNetworkCode(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }
}
