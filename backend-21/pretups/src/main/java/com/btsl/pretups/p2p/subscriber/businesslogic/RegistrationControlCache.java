package com.btsl.pretups.p2p.subscriber.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class RegistrationControlCache implements Runnable {

    private static Log _log = LogFactory.getLog(RegistrationControlCache.class.getName());
    private static HashMap _registerationControlMap = new HashMap();
    
    public void run() {
        try {
            Thread.sleep(50);
            refreshRegisterationControl();
        } catch (Exception e) {
        	 _log.error("RegistrationControlCache init() Exception ", e);
        }
    }
    private static SubscriberDAO _subscriberDAO = new SubscriberDAO();

    public static void refreshRegisterationControl() throws BTSLBaseException {
    	final String methodName = "refreshRegisterationControl";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            HashMap tempMap = null;
            final String loadType = null;
            if (_log.isDebugEnabled()) {
                _log.debug("refreshRegisterationControl", " Before loading:" + _registerationControlMap);
            }
            tempMap = _subscriberDAO.loadRegisterationControlCache();

            compareMaps(_registerationControlMap, tempMap);

            _registerationControlMap = tempMap;

            if (_log.isDebugEnabled()) {
                _log.debug("refreshRegisterationControl", " After loading:" + _registerationControlMap.size());
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
        	throw new BTSLBaseException("RegistrationControlCache", methodName, "Exception in refreshing Registeration Control");
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
                final String key = (String) iterator.next();
                final RegistrationControlVO prevVO = (RegistrationControlVO) p_previousMap.get(key);
                final RegistrationControlVO curVO = (RegistrationControlVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVO.getKey(), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null) {
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVO.getKey(), curVO.logInfo()));
                } else if (prevVO != null && curVO != null) {
                    if (!curVO.equalsRegistrationControlVO(prevVO)) {
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVO.getKey(), curVO.differences(prevVO)));
                    }
                }
            }

            // Note: this case arises when same number of element added and
            // deleted as well
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                final HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                final Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    final RegistrationControlVO mappingVO = (RegistrationControlVO) p_currentMap.get(iterator2.next());
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", mappingVO.getKey(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }

    public static RegistrationControlVO getRegistrationControlDetails(String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getRegistrationControlDetails()", "Entered with p_key=" + p_key);
        }
        return (RegistrationControlVO) _registerationControlMap.get(p_key);
    }
}
