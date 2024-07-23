package com.selftopup.pretups.routing.master.businesslogic;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.selftopup.common.ListValueVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

/**
 * 
 */
public class InterfaceRoutingControlCache {

    /**
     * Field _routingMap.
     */
    private static HashMap _routingMap = new HashMap();
    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(InterfaceRoutingControlCache.class.getName());
    /**
     * Field _routingControlDAO.
     */
    private static RoutingControlDAO _routingControlDAO = new RoutingControlDAO();

    /**
     * Method refreshSubscriberRoutingControl.
     */
    public static void refreshInterfaceRoutingControl() {
        if (_log.isDebugEnabled())
            _log.debug("refreshInterfaceRoutingControl", "Entered: ");
        try {
            HashMap tempMap = null;
            if (_log.isDebugEnabled())
                _log.debug("refreshInterfaceRoutingControl", " Before loading:" + _routingMap);
            tempMap = _routingControlDAO.loadInterfaceRoutingControlDetails();
            compareMaps(_routingMap, tempMap);
            _routingMap = tempMap;
            if (_log.isDebugEnabled())
                _log.debug("refreshInterfaceRoutingControl", " After loading:" + _routingMap.size());
        } catch (Exception e) {
            _log.error("refreshInterfaceRoutingControl", "Exception " + e.getMessage());
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
                ListValueVO prevVOAlt1 = null;
                ListValueVO prevVOAlt2 = null;
                ArrayList routingControlVOListPrev = (ArrayList) p_previousMap.get(key);
                if (routingControlVOListPrev != null && routingControlVOListPrev.size() > 0) {
                    prevVOAlt1 = (ListValueVO) routingControlVOListPrev.get(0);
                    if (routingControlVOListPrev.size() > 1)
                        prevVOAlt2 = (ListValueVO) routingControlVOListPrev.get(1);
                }

                ListValueVO curVOAlt1 = null;
                ListValueVO curVOAlt2 = null;
                ArrayList routingControlVOListCur = (ArrayList) p_currentMap.get(key);
                if (routingControlVOListCur != null && routingControlVOListCur.size() > 0) {
                    curVOAlt1 = (ListValueVO) routingControlVOListCur.get(0);
                    if (routingControlVOListCur.size() > 1)
                        curVOAlt2 = (ListValueVO) routingControlVOListCur.get(1);
                }

                if (prevVOAlt1 != null && curVOAlt1 == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVOAlt1.getCodeName(), ""));
                } else if (prevVOAlt1 == null && curVOAlt1 != null)
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVOAlt1.getCodeName(), ""));
                else if (prevVOAlt1 != null && curVOAlt1 != null) {
                    if (!curVOAlt1.equals(prevVOAlt1))
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVOAlt1.getCodeName(), ""));
                }
                if (prevVOAlt2 != null && curVOAlt2 == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVOAlt2.getCodeName(), ""));
                } else if (prevVOAlt2 == null && curVOAlt2 != null)
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVOAlt2.getCodeName(), ""));
                else if (prevVOAlt2 != null && curVOAlt2 != null) {
                    if (!curVOAlt2.equals(prevVOAlt2))
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVOAlt2.getCodeName(), ""));
                }

            }

            // Note: this case arises when same number of element added and
            // deleted as well
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
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
    public static ArrayList getRoutingControlDetails(String p_key) {
        if (_log.isDebugEnabled())
            _log.debug("getRoutingControlDetails()", "Entered p_key: " + p_key);
        return (ArrayList) _routingMap.get(p_key);
    }

}
