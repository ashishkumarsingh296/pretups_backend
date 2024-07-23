/**
 * @(#)ServiceKeywordCache.java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 * 
 *                              <description>
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              avinash.kamthan Mar 16, 2005 Initital Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 * 
 */
package com.selftopup.pretups.servicekeyword.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class ServiceKeywordCache {
    private static Log _log = LogFactory.getLog(ServiceKeywordCache.class.getName());

    private static HashMap _serviceKeywordMap = new HashMap();
    private static HashMap _serviceTypeMap = new HashMap();

    /**
     * To Load all prefrences on startup
     * Preferences like System level,Network Level,Zone Level,Service Level
     * 
     * void
     * PreferenceCache
     */
    public static void loadServiceKeywordCacheOnStartUp() {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceKeywordCacheOnStartUp()", "Entered");

        _serviceKeywordMap = loadServiceKeyword();
        _serviceTypeMap = loadServiceTypes();

        if (_log.isDebugEnabled())
            _log.debug("loadServiceKeywordCacheOnStartUp()", "Exited");

    }

    /**
     * To load the Preferences
     * 
     * @return
     *         HashMap
     *         PreferenceCache
     */
    private static HashMap loadServiceKeyword() {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceKeyword()", "Entered");

        ServiceKeywordDAO serviceDAO = new ServiceKeywordDAO();
        HashMap map = null;

        try {
            map = serviceDAO.loadServiceCache();
        } catch (Exception e) {
            _log.error("loadServiceKeyword() ", "Exception e: " + e.getMessage());
            e.printStackTrace();
        }

        if (_log.isDebugEnabled())
            _log.debug("loadServiceKeyword()", "Exited " + map.size());

        return map;
    }

    /**
     * To load the Preferences
     * 
     * @return
     *         HashMap
     *         PreferenceCache
     */
    private static HashMap loadServiceTypes() {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypes()", "Entered");

        ServiceKeywordDAO serviceDAO = new ServiceKeywordDAO();
        HashMap map = null;

        try {
            map = serviceDAO.loadServiceTypeCache();
        } catch (Exception e) {
            _log.error("loadServiceTypes() ", "Exception e: " + e.getMessage());
            e.printStackTrace();
        }

        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypes()", "Exited " + map.size());

        return map;
    }

    /**
     * To update the service keyword cache
     * 
     * void
     * ServiceKeywordCache
     */
    public static void updateServiceKeywords() {
        if (_log.isDebugEnabled())
            _log.debug("updateData()", " Entered");

        HashMap currentMap = loadServiceKeyword();

        if ((_serviceKeywordMap != null) && (_serviceKeywordMap.size() > 0)) {
            compareMaps(_serviceKeywordMap, currentMap);
        }

        _serviceKeywordMap = currentMap;
        _serviceTypeMap = loadServiceTypes();
        if (_log.isDebugEnabled())
            _log.debug("updateData()", "Exited " + _serviceKeywordMap.size());
    }

    /**
     * 
     * it returns the serviceKeywordVo agaubct the passed arguments
     * 
     * @param p_keyword
     * @param p_module
     * @param p_requestInterfaceType
     * @param p_servicePort
     * @return ServiceKeywordCacheVO
     */
    public static ServiceKeywordCacheVO getServiceKeywordObj(String p_keyword, String p_module, String p_requestInterfaceType, String p_servicePort) {

        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordObj()", "Entered p_keyword: " + p_keyword + " p_module: " + p_module + " p_requestInterfaceType: " + p_requestInterfaceType + " p_servicePort: " + p_servicePort);

        ServiceKeywordCacheVO serviceKeywordCacheVO = null;

        String key = p_keyword + "_" + p_module + "_" + p_requestInterfaceType + "_" + p_servicePort;

        serviceKeywordCacheVO = (ServiceKeywordCacheVO) _serviceKeywordMap.get(key);

        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordObj()", "Exiting serviceKeywordCacheVO: " + serviceKeywordCacheVO);

        return serviceKeywordCacheVO;
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
                ServiceKeywordCacheVO prevkeywordCacheVO = (ServiceKeywordCacheVO) p_previousMap.get(key);
                ServiceKeywordCacheVO curkeywordCacheVO = (ServiceKeywordCacheVO) p_currentMap.get(key);

                if ((prevkeywordCacheVO != null) && (curkeywordCacheVO == null)) {
                    isNewAdded = true;
                    CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Delete", prevkeywordCacheVO.getServiceTypeKeyword(), prevkeywordCacheVO.logInfo()));
                } else if ((prevkeywordCacheVO == null) && (curkeywordCacheVO != null)) {
                    CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Add", curkeywordCacheVO.getServiceTypeKeyword(), curkeywordCacheVO.logInfo()));
                } else if ((prevkeywordCacheVO != null) && (curkeywordCacheVO != null)) {
                    if (!curkeywordCacheVO.equals(prevkeywordCacheVO)) {
                        CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Modify", curkeywordCacheVO.getServiceTypeKeyword(), curkeywordCacheVO.differences(prevkeywordCacheVO)));
                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted as well
             */
            if ((p_previousMap.size() == p_currentMap.size()) && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);

                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();

                while (iterator2.hasNext()) {
                    // new added
                    ServiceKeywordCacheVO keywordCacheVO = (ServiceKeywordCacheVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Add", keywordCacheVO.getServiceTypeKeyword(), keywordCacheVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }

    public static HashMap getServiceKeywordMap() {
        return _serviceKeywordMap;
    }

    /**
     * Method to load the service keywod Type based on cache
     * 
     * @param p_requestVO
     * @return ServiceKeywordCacheVO
     *         CR 000009 Sub Keyword Logic and removing the sub keyword from
     *         request message
     */
    public static ServiceKeywordCacheVO getServiceKeywordObj(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordObj()", "Entered p_requestVO: ");
        ServiceKeywordCacheVO serviceKeywordCacheVO = null;
        String key = p_requestVO.getRequestMessageArray()[0].toUpperCase() + "_" + p_requestVO.getModule() + "_" + p_requestVO.getRequestGatewayType() + "_" + p_requestVO.getServicePort();
        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordObj()", "key=" + key);
        serviceKeywordCacheVO = (ServiceKeywordCacheVO) _serviceKeywordMap.get(key);
        if (serviceKeywordCacheVO != null) {
            ArrayList subKeywordList = serviceKeywordCacheVO.getSubKeywordList();
            if (subKeywordList != null && !subKeywordList.isEmpty()) {

                int size = subKeywordList.size();
                if (_log.isDebugEnabled())
                    _log.debug("getServiceKeywordObj()", "Sub Keyword =" + p_requestVO.getRequestMessageArray()[1].toUpperCase());
                serviceKeywordCacheVO = null;
                for (int i = 0; i < size; i++) {
                    serviceKeywordCacheVO = (ServiceKeywordCacheVO) subKeywordList.get(i);
                    if (serviceKeywordCacheVO.getKeyword().equals(p_requestVO.getRequestMessageArray()[0].toUpperCase()) && serviceKeywordCacheVO.getSubKeyword().equals(p_requestVO.getRequestMessageArray()[1].toUpperCase()) && serviceKeywordCacheVO.getModule().equals(p_requestVO.getModule()) && serviceKeywordCacheVO.getRequestInterfaceType().equals(p_requestVO.getRequestGatewayType()) && serviceKeywordCacheVO.getServerPort().equals(p_requestVO.getServicePort())) {
                        break;
                    }
                    serviceKeywordCacheVO = null;
                }
                if (serviceKeywordCacheVO != null) {
                    String[] source = p_requestVO.getRequestMessageArray();
                    String[] target = new String[source.length - 1];
                    target[0] = serviceKeywordCacheVO.getServiceType();
                    System.arraycopy(source, 2, target, 1, source.length - 2);
                    p_requestVO.setRequestMessageArray(target);
                }
            } else if (serviceKeywordCacheVO.isSubKeywordApplicable()) {
                if (_log.isDebugEnabled())
                    _log.debug("getServiceKeywordObj()", "Sub Keyword =" + p_requestVO.getRequestMessageArray()[1].toUpperCase());
                if (serviceKeywordCacheVO.getKeyword().equals(p_requestVO.getRequestMessageArray()[0].toUpperCase()) && serviceKeywordCacheVO.getSubKeyword().equals(p_requestVO.getRequestMessageArray()[1].toUpperCase()) && serviceKeywordCacheVO.getModule().equals(p_requestVO.getModule()) && serviceKeywordCacheVO.getRequestInterfaceType().equals(p_requestVO.getRequestGatewayType()) && serviceKeywordCacheVO.getServerPort().equals(p_requestVO.getServicePort())) {
                    String[] source = p_requestVO.getRequestMessageArray();
                    String[] target = new String[source.length - 1];
                    target[0] = serviceKeywordCacheVO.getServiceType();
                    System.arraycopy(source, 2, target, 1, source.length - 2);
                    p_requestVO.setRequestMessageArray(target);
                } else
                    serviceKeywordCacheVO = null;
            }
        }

        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordObj()", "Exiting serviceKeywordCacheVO: " + serviceKeywordCacheVO);
        return serviceKeywordCacheVO;
    }

    /**
     * Method to get the service type object
     * 
     * @param p_serviceTypes
     * @param p_module
     * @return
     */
    public static ServiceKeywordCacheVO getServiceTypeObject(String p_serviceTypes, String p_module) {
        if (_log.isDebugEnabled())
            _log.debug("getServiceTypeObject()", "Entered p_serviceTypes: " + p_serviceTypes + " p_module:" + p_module);
        ServiceKeywordCacheVO serviceKeywordCacheVO = null;
        String key = p_serviceTypes + "_" + p_module;
        serviceKeywordCacheVO = (ServiceKeywordCacheVO) _serviceTypeMap.get(key);
        if (_log.isDebugEnabled())
            _log.debug("getServiceTypeObject()", "Exiting serviceKeywordCacheVO: " + serviceKeywordCacheVO);
        return serviceKeywordCacheVO;
    }
}
