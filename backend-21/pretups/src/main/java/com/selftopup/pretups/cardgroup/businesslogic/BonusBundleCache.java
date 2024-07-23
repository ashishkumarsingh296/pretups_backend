package com.selftopup.pretups.cardgroup.businesslogic;

/**
 * @(#)BonusBundleCache.java
 *                           Copyright(c) 2009, Comviva Technologies Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Vinay Kumar Singh July 30, 2009 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.sql.Connection;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.OracleUtil;

public class BonusBundleCache {
    private static Log _log = LogFactory.getLog(BonusBundleCache.class.getName());
    private static ArrayList _bonusBundleList = new ArrayList();
    private static HashMap _bonusBundleMappingMap = new HashMap();
    private static HashMap _bonusBundleMappingLinkedMap = new LinkedHashMap();

    public static void loadBonusBundleCacheOnStartUp() {

        if (_log.isDebugEnabled())
            _log.debug("loadBonusBundleCacheOnStartUp()", "Entered");
        // Load the Bonus bundle mapping.
        getBonusBundleMapping();

        if (_log.isDebugEnabled())
            _log.debug("loadBonusBundleCacheOnStartUp()", "Exited");
    }

    /**
     * Load the cache for the Bonus Bundle Mapping
     * 
     * @return
     */
    private static void getBonusBundleMapping() {
        if (_log.isDebugEnabled())
            _log.debug("getBonusBundleMapping", "Entered");
        int listSize = 0;
        BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
        BonusBundleDetailVO bonusBundleDetailVO = null;
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            _bonusBundleList = bonusBundleDAO.loadBonusBundles(con);
            listSize = _bonusBundleList.size();
            if (_bonusBundleList != null && listSize > 0) {
                for (int i = 0; i < listSize; i++) {
                    bonusBundleDetailVO = (BonusBundleDetailVO) _bonusBundleList.get(i);
                    _bonusBundleMappingMap.put(bonusBundleDetailVO.getBundleID(), bonusBundleDetailVO);
                    _bonusBundleMappingLinkedMap.put(bonusBundleDetailVO.getBundleID(), bonusBundleDetailVO);
                }
            }
        } catch (Exception e) {
            _log.error("getBonusBundleMapping", "Exception: " + e.getMessage());
            _log.errorTrace("getBonusBundleMapping: Exception print stack trace:", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getBonusBundleMapping", "Exited with _bonusBundleList size=" + _bonusBundleList.size());
        }

    }

    /**
     * Method to return the Bonus Bundle List containing the bonus bundle
     * mapping info
     * 
     * @return
     */
    public static ArrayList getBonusBundleList() {
        return _bonusBundleList;
    }

    /**
     * Method to return the Master Map containing the Bundle Type and Bonus
     * Bundles VO
     * 
     * @return
     */
    public static HashMap getBonusBundlesMap() {
        return _bonusBundleMappingMap;
    }

    public static HashMap getBonusBundlesLinkedMap() {
        return _bonusBundleMappingLinkedMap;
    }

    /**
     * Method to get the Bonus Bundle DetailVO For Bundle ID
     * 
     * @param p_bundleID
     *            String
     * @return bonusBundleDetailVO BonusBundleDetailVO
     */
    public static BonusBundleDetailVO getBonusBundleDetailVO(String p_bundleID) {
        if (_log.isDebugEnabled())
            _log.debug("getBonusBundleDetailVO()", "Entered for p_bundleID=" + p_bundleID);
        BonusBundleDetailVO bonusBundleDetailVO = null;
        try {
            bonusBundleDetailVO = (BonusBundleDetailVO) _bonusBundleMappingMap.get(p_bundleID);
        } catch (Exception e) {
            _log.error("getBonusBundleDetailVO()", "Exception : " + e);
            _log.errorTrace("getBonusBundleDetailVO: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingCache[getDefaultSelectorForServiceType]", "", "", "", "Exception:" + e.getMessage() + " Check the configuration for service selector mapping");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getBonusBundleDetailVO()", "Exiting for p_bundleID=" + p_bundleID + " with bonusBundleDetailVO=" + bonusBundleDetailVO);
        }
        return bonusBundleDetailVO;
    }
}
