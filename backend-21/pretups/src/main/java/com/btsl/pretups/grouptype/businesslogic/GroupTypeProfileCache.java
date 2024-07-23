/**
 * @(#)GroupTypeProfileCache.java
 *                                Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 * 
 *                                <description>
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Ankit Zindal July 11, 2006 Initital Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 * 
 */

package com.btsl.pretups.grouptype.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.util.BTSLUtil;

public class GroupTypeProfileCache implements Runnable {
    private static Log _log = LogFactory.getLog(GroupTypeProfileCache.class.getName());

    private static HashMap _grptProfileMap = new HashMap();
    
    public void run() {
        try {
            Thread.sleep(50);
            loadGroupTypeProfilesAtStartup();
        } catch (Exception e) {
        	 _log.error("GroupTypeProfileCache init() Exception ", e);
        }
    }

    public static void loadGroupTypeProfilesAtStartup() throws BTSLBaseException {
    	final String methodName = "loadGroupTypeProfilesAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            GroupTypeDAO grptDAO = new GroupTypeDAO();
            _grptProfileMap = grptDAO.loadGroupTypeProfileCache();
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
        	throw new BTSLBaseException("GroupTypeProfileCache", methodName, "Exception in loading Group Type Profile at Startup");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * to update the cache
     * 
     * void
     * @throws Exception 
     */
    public static void updateGroupTypeProfilesCache() throws BTSLBaseException {
    	final String methodName = "updateGroupTypeProfilesCache";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            GroupTypeDAO grptDAO = new GroupTypeDAO();
            HashMap currentMap = grptDAO.loadGroupTypeProfileCache();
            if (_grptProfileMap != null && _grptProfileMap.size() > 0) {
                compareMaps(_grptProfileMap, currentMap);
            }
            _grptProfileMap = currentMap;
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
        	throw new BTSLBaseException("GroupTypeProfileCache", methodName, "Exception in updating Group Type Profile at Startup");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + _grptProfileMap.size());
        	}
        }
    }

    /**
     * 
     * @param p_networkID
     * @param p_groupType
     * @param p_reqGatewayCode
     * @param p_resGatyewayCode
     * @param p_type
     * @return
     */
    public static Object getObject(String p_networkID, String p_groupType, String p_reqGatewayCode, String p_resGatyewayCode, String p_type) {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered p_networkID:" + p_networkID + " p_groupType:" + p_groupType + " p_reqGatewayCode:" + p_reqGatewayCode + " p_resGatyewayCode:" + p_resGatyewayCode + " p_type:" + p_type);
        }
        GroupTypeProfileVO grptProfileVO = null;
        String key = p_networkID + "_" + p_groupType + "_" + p_reqGatewayCode + "_" + p_resGatyewayCode + "_" + p_type;
        grptProfileVO = (GroupTypeProfileVO) _grptProfileMap.get(key);
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Exited " + grptProfileVO);
        }
        return grptProfileVO;
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

        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
        }

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
        while (iterator != null && iterator.hasNext()) {
            String key = (String) iterator.next();
            GroupTypeProfileVO prevGroupTypeProfileVO = (GroupTypeProfileVO) p_previousMap.get(key);
            GroupTypeProfileVO curGroupTypeProfileVO = (GroupTypeProfileVO) p_currentMap.get(key);
            if (prevGroupTypeProfileVO != null && curGroupTypeProfileVO == null) {
                isNewAdded = true;
                CacheOperationLog.log("GroupTypeProfileCache", BTSLUtil.formatMessage("Delete", key, prevGroupTypeProfileVO.logInfo()));
            } else {
                if (prevGroupTypeProfileVO == null && curGroupTypeProfileVO != null) {
                    // new network added
                    CacheOperationLog.log("GroupTypeProfileCache", BTSLUtil.formatMessage("Add", key, curGroupTypeProfileVO.logInfo()));
                } else if (prevGroupTypeProfileVO != null && curGroupTypeProfileVO != null) {
                    if (!curGroupTypeProfileVO.equals(prevGroupTypeProfileVO)) {
                        CacheOperationLog.log("GroupTypeProfileCache", BTSLUtil.formatMessage("Modify", key, curGroupTypeProfileVO.differences(prevGroupTypeProfileVO)));
                    }
                }
            }
        }
        if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
            HashMap tempMap = new HashMap(p_currentMap);
            while (copiedIterator.hasNext()) {
                tempMap.remove(copiedIterator.next());
            }
            Iterator iterator2 = tempMap.keySet().iterator();
            while (iterator2.hasNext()) {
                // new network added
                GroupTypeProfileVO profileVO = (GroupTypeProfileVO) p_currentMap.get(iterator2.next());
                CacheOperationLog.log("GroupTypeProfileCache", BTSLUtil.formatMessage("Add", (String) iterator2.next(), profileVO.logInfo()));
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }
}
