package com.btsl.pretups.master.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * RequestInterfaceCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 30/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Clss to load the Request interface
 */

public class RequestInterfaceCache {

    private static Log _log = LogFactory.getLog(RequestInterfaceCache.class.getName());
    private static HashMap _requestInterfaceMap = new HashMap();
    private static RequestInterfaceDAO _requestInterfaceDAO = new RequestInterfaceDAO();

    /**
	 * ensures no instantiation
	 */
    private RequestInterfaceCache(){
    	
    }
    public static void refreshRequestInterface() {
        final String METHOD_NAME = "refreshRequestInterface";
        if (_log.isDebugEnabled()) {
            _log.debug("refreshRequestInterface", "Entered: ");
        }
        try {
            HashMap tempMap = null;
            // String loadType=null;
            if (_log.isDebugEnabled()) {
                _log.debug("refreshRequestInterface", " Before loading:" + _requestInterfaceMap);
            }
            tempMap = _requestInterfaceDAO.loadRequestInterfaceDetails();
            compareMaps(_requestInterfaceMap, tempMap);
            _requestInterfaceMap = tempMap;
            if (_log.isDebugEnabled()) {
                _log.debug("refreshRequestInterface", " After loading:" + _requestInterfaceMap.size());
            }
        } catch (Exception e) {
            _log.error("refreshRequestInterface", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * To compare Maps
     * 
     * @param p_previousMap
     * @param p_currentMap
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        final String METHOD_NAME = "compareMaps";
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
        }
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
                RequestInterfaceDetailVO prevVO = (RequestInterfaceDetailVO) p_previousMap.get(key);
                RequestInterfaceDetailVO curVO = (RequestInterfaceDetailVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVO.getReqInterfaceCode(), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null) {
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVO.getReqInterfaceCode(), curVO.logInfo()));
                } else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO)) {
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVO.getReqInterfaceCode(), curVO.differences(prevVO)));
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
                    RequestInterfaceDetailVO mappingVO = (RequestInterfaceDetailVO) p_currentMap.get(iterator2.next());
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", mappingVO.getReqInterfaceCode(), mappingVO.logInfo()));
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
     * Method to get the Request Interface Details from cache
     * 
     * @param p_requestInterfaceCode
     * @return RequestInterfaceDetailVO
     */
    public static RequestInterfaceDetailVO getRequestInterfaceDetails(String p_requestInterfaceCode) {
        return (RequestInterfaceDetailVO) _requestInterfaceMap.get(p_requestInterfaceCode);
    }
}
