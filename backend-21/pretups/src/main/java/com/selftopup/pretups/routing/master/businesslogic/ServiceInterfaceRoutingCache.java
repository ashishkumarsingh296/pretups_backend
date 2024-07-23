package com.selftopup.pretups.routing.master.businesslogic;

/*
 * @# ServiceInterfaceRoutingCache
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Ankit Singhal 17/05/2006 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 */

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

public class ServiceInterfaceRoutingCache {

    /**
     * Field _routingMap.
     */
    private static HashMap _serviceInterfaceRoutingMap = new HashMap();
    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(ServiceInterfaceRoutingCache.class.getName());

    /**
     * Method refreshServiceRouting.
     */
    public static void refreshServiceInterfaceRouting() {
        if (_log.isDebugEnabled())
            _log.debug("refreshServiceInterfaceRouting", "Entered: ");
        try {
            HashMap tempMap = null;
            if (_log.isDebugEnabled())
                _log.debug("refreshServiceInterfaceRouting", " Before loading:" + _serviceInterfaceRoutingMap);
            tempMap = new RoutingControlDAO().loadServiceInterfaceRoutingDetails();
            compareMaps(_serviceInterfaceRoutingMap, tempMap);
            _serviceInterfaceRoutingMap = tempMap;
            if (_log.isDebugEnabled())
                _log.debug("refreshServiceInterfaceRouting", " After loading:" + _serviceInterfaceRoutingMap.size());
        } catch (Exception e) {
            _log.error("refreshServiceInterfaceRouting", "Exception " + e.getMessage());
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
                ServiceInterfaceRoutingVO prevVO = (ServiceInterfaceRoutingVO) p_previousMap.get(key);
                ServiceInterfaceRoutingVO curVO = (ServiceInterfaceRoutingVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVO.getInterfaceType(), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null)
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVO.getInterfaceType(), curVO.logInfo()));
                else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO))
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVO.getInterfaceType(), curVO.differences(prevVO)));
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
                    ServiceInterfaceRoutingVO mappingVO = (ServiceInterfaceRoutingVO) p_currentMap.get(iterator2.next());
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", mappingVO.getInterfaceType(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }

    /**
     * Method getRoutingControlDetails.
     * 
     * @param p_key
     *            String
     * @return SubscriberRoutingControlVO
     */
    public static ServiceInterfaceRoutingVO getServiceInterfaceRoutingDetails(String p_key) {
        if (_log.isDebugEnabled())
            _log.debug("getServiceInterfaceRoutingDetails()", "Entered with key=" + p_key);
        return (ServiceInterfaceRoutingVO) _serviceInterfaceRoutingMap.get(p_key);
    }

}
