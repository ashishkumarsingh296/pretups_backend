package com.btsl.ota.services.businesslogic;

/*
 * SimProfileCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 31/08/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to cache the Sim Profile List
 */

import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.util.BTSLUtil;

public class SimProfileCache implements Runnable  {

    private static Log _log = LogFactory.getLog(SimProfileCache.class.getName());
    private static HashMap _simProfileMap = new HashMap();
    private static ServicesDAO _servicesDAO = new ServicesDAO();
    
	public void run() {
		try {
			Thread.sleep(50);
			refreshSimProfileList();
		} catch (Exception e) {
			_log.error("refreshSimProfileList init() Exception ", e);
		}
	}


    public static void refreshSimProfileList() throws BTSLBaseException {
    	final String methodName = "refreshSimProfileList";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            HashMap tempMap = null;
            String loadType = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Before loading:" + _simProfileMap);
            }
            tempMap = _servicesDAO.loadSimProfileList();
            compareMaps(_simProfileMap, tempMap);
            _simProfileMap = tempMap;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " After loading:" + _simProfileMap.size());
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
        	throw new BTSLBaseException("SimProfileCache", methodName, "Exception in refreshing SimProfileList.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To compare Maps
     * 
     * @param p_previousMap
     * @param p_currentMap
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }
        final String METHOD_NAME = "compareMaps";
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
            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                SimProfileVO prevVO = (SimProfileVO) p_previousMap.get(key);
                SimProfileVO curVO = (SimProfileVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("SimProfileCache", BTSLUtil.formatMessage("Delete", prevVO.getSimID(), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null) {
                    CacheOperationLog.log("SimProfileCache", BTSLUtil.formatMessage("Add", curVO.getSimID(), curVO.logInfo()));
                } else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO)) {
                        CacheOperationLog.log("SimProfileCache", BTSLUtil.formatMessage("Modify", curVO.getSimID(), curVO.differences(prevVO)));
                    }
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
                    SimProfileVO mappingVO = (SimProfileVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("SimProfileCache", BTSLUtil.formatMessage("Add", mappingVO.getSimID(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }

    /**
     * Method to get the Sim profile Details
     * 
     * @param p_simID
     * @return SimProfileVO
     */
    public static SimProfileVO getSimProfileDetails(String p_simID) {
        return (SimProfileVO) _simProfileMap.get(p_simID);
    }

}
