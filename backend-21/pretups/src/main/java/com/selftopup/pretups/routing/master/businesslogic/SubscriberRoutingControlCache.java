/*
 * @# SubscriberRoutingControlCache.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Oct 30, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.routing.master.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

/**
 * 
 */
public class SubscriberRoutingControlCache {

    /**
     * Field _routingMap.
     */
    private static HashMap _routingMap = new HashMap();
    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(SubscriberRoutingControlCache.class.getName());
    /**
     * Field _routingControlDAO.
     */
    private static RoutingControlDAO _routingControlDAO = new RoutingControlDAO();

    /**
     * Method refreshSubscriberRoutingControl.
     */
    public static void refreshSubscriberRoutingControl() {
        if (_log.isDebugEnabled())
            _log.debug("refreshSubscriberRoutingControl", "Entered: ");
        try {
            HashMap tempMap = null;
            if (_log.isDebugEnabled())
                _log.debug("refreshSubscriberRoutingControl", " Before loading:" + _routingMap);
            tempMap = _routingControlDAO.loadRoutingControlDetails();
            compareMaps(_routingMap, tempMap);
            _routingMap = tempMap;
            if (_log.isDebugEnabled())
                _log.debug("refreshSubscriberRoutingControl", " After loading:" + _routingMap.size());
        } catch (Exception e) {
            _log.error("refreshSubscriberRoutingControl", "Exception " + e.getMessage());
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
                SubscriberRoutingControlVO prevVO = (SubscriberRoutingControlVO) p_previousMap.get(key);
                SubscriberRoutingControlVO curVO = (SubscriberRoutingControlVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVO.getInterfaceCategory(), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null)
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVO.getInterfaceCategory(), curVO.logInfo()));
                else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO))
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVO.getInterfaceCategory(), curVO.differences(prevVO)));
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
                    SubscriberRoutingControlVO mappingVO = (SubscriberRoutingControlVO) p_currentMap.get(iterator2.next());
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", mappingVO.getInterfaceCategory(), mappingVO.logInfo()));
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
    public static SubscriberRoutingControlVO getRoutingControlDetails(String p_key) {
        if (_log.isDebugEnabled())
            _log.debug("getRoutingControlDetails()", "Entered p_key: " + p_key);
        return (SubscriberRoutingControlVO) _routingMap.get(p_key);
    }

}
