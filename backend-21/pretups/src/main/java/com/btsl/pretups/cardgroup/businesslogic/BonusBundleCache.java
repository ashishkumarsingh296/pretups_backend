package com.btsl.pretups.cardgroup.businesslogic;

import java.sql.Connection;
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

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.OracleUtil;

/**
 * @author
 *
 */
public class BonusBundleCache implements Runnable{
    private static Log _log = LogFactory.getLog(BonusBundleCache.class.getName());
    private static ArrayList _bonusBundleList = new ArrayList();
    private static HashMap _bonusBundleMappingMap = new HashMap();
    private static HashMap _bonusBundleMappingLinkedMap = new LinkedHashMap();
    private static final String CLASS_NAME = "BonusBundleCache";
    
    public void run() {
        try {
            Thread.sleep(50);
            loadBonusBundleCacheOnStartUp();
        } catch (Exception e) {
        	 _log.error("BonusBundleCache init() Exception ", e);
        }
    }

    /**
     * @throws Exception 
     * 
     */
    public static void loadBonusBundleCacheOnStartUp() throws BTSLBaseException {
    	final String methodName = "loadBonusBundleCacheOnStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
	    try{
	        // Load the Bonus bundle mapping.
	        getBonusBundleMapping();
	    }
	    catch(BTSLBaseException be) {
	    	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
	    	_log.errorTrace(methodName, be);
	    	throw be;
	    }
	    catch (Exception e)
	    {
	    	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	    	_log.errorTrace(methodName, e);
	    	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in Loading the cache for the Bonus Bundle Mapping on Startup.");
	    } finally {
	    	if (_log.isDebugEnabled()) {
	    		_log.debug(methodName, PretupsI.EXITED);
	    	}
	    }
    }

    /**
     * Load the cache for the Bonus Bundle Mapping
     * 
     * @return
     * @throws Exception 
     */
    private static void getBonusBundleMapping() throws BTSLBaseException {
    	final String methodName = "getBonusBundleMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        int listSize = 0;
        
        final BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
        BonusBundleDetailVO bonusBundleDetailVO = null;
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            _bonusBundleList = bonusBundleDAO.loadBonusBundles(con);
            listSize = _bonusBundleList.size();
            if (_bonusBundleList != null && listSize > 0) {
                for (int i = 0; i < listSize; i++) {
                    bonusBundleDetailVO = (BonusBundleDetailVO) _bonusBundleList.get(i);
                    _bonusBundleMappingMap.put(bonusBundleDetailVO.getBundleID(), bonusBundleDetailVO);
                    _bonusBundleMappingLinkedMap.put(bonusBundleDetailVO.getBundleID(), bonusBundleDetailVO);
                }
            }
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in Loading the cache for the Bonus Bundle Mapping.");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, PretupsI.EXITED + " with _bonusBundleList size=" + _bonusBundleList.size());
            }
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
     * @param pBundleID
     *            String
     * @return bonusBundleDetailVO BonusBundleDetailVO
     */
    public static BonusBundleDetailVO getBonusBundleDetailVO(String pBundleID) {
    	final String methodName = "getBonusBundleDetailVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered for pBundleID=" + pBundleID);
        }
        
        BonusBundleDetailVO bonusBundleDetailVO = null;
        try {
            bonusBundleDetailVO = (BonusBundleDetailVO) _bonusBundleMappingMap.get(pBundleID);
        } catch (Exception e) {
            _log.error("getBonusBundleDetailVO()", "Exception : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ServiceSelectorMappingCache[getDefaultSelectorForServiceType]", "", "", "",
                "Exception:" + e.getMessage() + " Check the configuration for service selector mapping");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getBonusBundleDetailVO()", "Exiting for pBundleID=" + pBundleID + " with bonusBundleDetailVO=" + bonusBundleDetailVO);
            }
        }
        return bonusBundleDetailVO;
    }
}
