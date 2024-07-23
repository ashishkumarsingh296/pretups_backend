/**
 * @(#)MessageGatewayCache.java
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
 *                              avinash.kamthan Jul 11, 2005 Initital Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 * 
 */

package com.selftopup.pretups.gateway.businesslogic;

import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 */
public class MessageGatewayCache {

    private static Log _log = LogFactory.getLog(MessageGatewayCache.class.getName());

    private static HashMap _messageGatewayMap = new HashMap();

    private static HashMap _messageGatewayMappingMap = new HashMap();

    public static void loadMessageGatewayAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadMessageGatewayAtStartup()", "entered");
        _messageGatewayMap = loadMessageGateway();
        _messageGatewayMappingMap = loadMessageGatewayMapping();
        if (_log.isDebugEnabled())
            _log.debug("loadMessageGatewayAtStartup()", "exited");
    }

    /**
     * To load the gateway details
     * 
     * @return HashMap
     */
    private static HashMap loadMessageGateway() {
        if (_log.isDebugEnabled())
            _log.debug("loadMessageGateway()", "entered");
        MessageGatewayDAO gatewayDAO = new MessageGatewayDAO();
        HashMap map = null;
        try {
            map = gatewayDAO.loadMessageGatewayCache();
        } catch (Exception e) {
            _log.error("loadMessageGateway()", "Exception: " + e.getMessage());
            _log.errorTrace("loadMessageGateway: Exception print stack trace:", e);
        }
        if (_log.isDebugEnabled())
            _log.debug("loadMessageGateway()", "exited");

        return map;
    }

    /**
     * To load the mapping details
     * 
     * @return HashMap
     */
    private static HashMap loadMessageGatewayMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadMessageGatewayMapping()", "entered");

        MessageGatewayDAO gatewayDAO = new MessageGatewayDAO();
        HashMap map = null;
        try {
            map = gatewayDAO.loadMessageGatewayMappingCache();
        } catch (Exception e) {
            _log.errorTrace("loadMessageGatewayMapping: Exception print stack trace:", e);
            _log.error("loadMessageGatewayMapping()", "Exception e:" + e.getMessage());
        }
        if (_log.isDebugEnabled())
            _log.debug("loadMessageGatewayMapping()", "exited");

        return map;
    }

    /**
     * to update the cache
     */
    public static void updateMessageGateway() {

        if (_log.isDebugEnabled())
            _log.debug("updateMessageGateway()", " Entered");
        HashMap currentMap = loadMessageGateway();

        if (_messageGatewayMap != null && _messageGatewayMap.size() > 0) {
            compareMaps(_messageGatewayMap, currentMap);
        }

        _messageGatewayMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateMessageGateway()", "exited " + _messageGatewayMap.size());
    }

    /**
     * to update the cache
     */
    public static void updateMessageGatewayMapping() {

        if (_log.isDebugEnabled())
            _log.debug("updateMessageGatewayMapping()", " Entered");
        HashMap currentMap = loadMessageGatewayMapping();

        if (_messageGatewayMappingMap != null && _messageGatewayMappingMap.size() > 0) {
            compareMappingMaps(_messageGatewayMappingMap, currentMap);
        }

        _messageGatewayMappingMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateMessageGatewayMapping()", "exited " + _messageGatewayMappingMap.size());
    }

    /**
     * get the massagegateway vo from cache
     * 
     * @param p_messageGatewayCode
     * @return MessageGatewayVO
     */
    public static MessageGatewayVO getObject(String p_messageGatewayCode) {

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "entered " + p_messageGatewayCode);
        MessageGatewayVO messageGatewayVO = null;
        messageGatewayVO = (MessageGatewayVO) _messageGatewayMap.get(p_messageGatewayCode);
        if (_log.isDebugEnabled())
            _log.debug("getObject()", "exited " + messageGatewayVO);
        return messageGatewayVO;
    }

    /**
     * @param p_requestCode
     * @return MessageGatewayMappingCacheVO
     */
    public static MessageGatewayMappingCacheVO getMappingObject(String p_requestCode) {

        if (_log.isDebugEnabled())
            _log.debug("updateData()", "entered " + p_requestCode);
        MessageGatewayMappingCacheVO messageMappingCacheVO = null;

        messageMappingCacheVO = (MessageGatewayMappingCacheVO) _messageGatewayMappingMap.get(p_requestCode);

        if (_log.isDebugEnabled())
            _log.debug("updateData()", "exited " + messageMappingCacheVO);

        return messageMappingCacheVO;
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

        try {
            if (_log.isDebugEnabled())
                _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);

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

            // to check whether any new Message Cache added or not but size of
            boolean isNewAdded = false;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                MessageGatewayVO prevMessageGatewayVO = (MessageGatewayVO) p_previousMap.get(key);
                MessageGatewayVO curMessageGatewayVO = (MessageGatewayVO) p_currentMap.get(key);

                if (prevMessageGatewayVO != null && curMessageGatewayVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete", prevMessageGatewayVO.getGatewayCode(), prevMessageGatewayVO.logInfo()));
                    if (prevMessageGatewayVO.getRequestGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Request Info ", prevMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), prevMessageGatewayVO.getRequestGatewayVO().logInfo()));
                    }
                    if (prevMessageGatewayVO.getResponseGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Response Info ", prevMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), prevMessageGatewayVO.getResponseGatewayVO().logInfo()));
                    }

                } else if (prevMessageGatewayVO == null && curMessageGatewayVO != null) {
                    CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add", curMessageGatewayVO.getGatewayCode(), curMessageGatewayVO.logInfo()));

                    if (curMessageGatewayVO.getRequestGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Request Info ", curMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), curMessageGatewayVO.getRequestGatewayVO().logInfo()));
                    }
                    if (curMessageGatewayVO.getResponseGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Response Info ", curMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), curMessageGatewayVO.getResponseGatewayVO().logInfo()));
                    }
                } else if (prevMessageGatewayVO != null && curMessageGatewayVO != null) {
                    if (!curMessageGatewayVO.equals(prevMessageGatewayVO)) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Modify", curMessageGatewayVO.getGatewayCode(), curMessageGatewayVO.differences(prevMessageGatewayVO)));

                        // log the request vo modification
                        if (curMessageGatewayVO.getRequestGatewayVO() != null && prevMessageGatewayVO.getRequestGatewayVO() == null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Request Info ", curMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), curMessageGatewayVO.getRequestGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getRequestGatewayVO() == null && prevMessageGatewayVO.getRequestGatewayVO() != null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Request Info ", prevMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), prevMessageGatewayVO.getRequestGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getRequestGatewayVO() != null && prevMessageGatewayVO.getRequestGatewayVO() != null && !curMessageGatewayVO.getRequestGatewayVO().equals(prevMessageGatewayVO.getRequestGatewayVO())) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Modify Request Info ", curMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), curMessageGatewayVO.getRequestGatewayVO().differences(prevMessageGatewayVO.getRequestGatewayVO())));
                        }

                        // log the response vo modification
                        if (curMessageGatewayVO.getResponseGatewayVO() != null && prevMessageGatewayVO.getResponseGatewayVO() == null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Response Info ", curMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), curMessageGatewayVO.getResponseGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getResponseGatewayVO() == null && prevMessageGatewayVO.getResponseGatewayVO() != null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Response Info ", prevMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), prevMessageGatewayVO.getResponseGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getResponseGatewayVO() != null && prevMessageGatewayVO.getResponseGatewayVO() != null && !curMessageGatewayVO.getResponseGatewayVO().equals(prevMessageGatewayVO.getResponseGatewayVO())) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Modify Response Info ", curMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), curMessageGatewayVO.getResponseGatewayVO().differences(prevMessageGatewayVO.getResponseGatewayVO())));
                        }
                    }
                }
            }

            /**
             * Note: this case arises when same number of messagegateway added
             * and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    // new network added
                    MessageGatewayVO messageGatewayVO = (MessageGatewayVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add", messageGatewayVO.getNetworkCode(), messageGatewayVO.logInfo()));

                    if (messageGatewayVO.getRequestGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Request Info ", messageGatewayVO.getRequestGatewayVO().getGatewayCode(), messageGatewayVO.getRequestGatewayVO().logInfo()));
                    }
                    if (messageGatewayVO.getResponseGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Response Info ", messageGatewayVO.getResponseGatewayVO().getGatewayCode(), messageGatewayVO.getResponseGatewayVO().logInfo()));
                    }
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug("compareMaps()", "Exited");
            }
        } catch (Exception e) {
            _log.errorTrace("compareMaps: Exception print stack trace:", e);
        }

    }

    private static void compareMappingMaps(HashMap p_previousMap, HashMap p_currentMap) {

        try {
            if (_log.isDebugEnabled())
                _log.debug("compareMappingMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
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
                MessageGatewayMappingCacheVO prevMappingVO = (MessageGatewayMappingCacheVO) p_previousMap.get(key);
                MessageGatewayMappingCacheVO curMappingVO = (MessageGatewayMappingCacheVO) p_currentMap.get(key);

                if (prevMappingVO != null && curMappingVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    isNewAdded = true;
                    CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Delete", prevMappingVO.getRequestCode(), prevMappingVO.logInfo()));
                } else if (prevMappingVO == null && curMappingVO != null) {
                    // new network added
                    CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Add", curMappingVO.getRequestCode(), curMappingVO.logInfo()));
                } else if (prevMappingVO != null && curMappingVO != null) {
                    if (!curMappingVO.equals(prevMappingVO)) {
                        CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Modify", curMappingVO.getRequestCode(), curMappingVO.differences(prevMappingVO)));

                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    // new network added
                    MessageGatewayMappingCacheVO mappingVO = (MessageGatewayMappingCacheVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Add", mappingVO.getRequestCode(), mappingVO.logInfo()));
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("compareMappingMaps()", "Exited");
            }
        } catch (Exception e) {
            _log.errorTrace("compareMappingMaps: Exception print stack trace:", e);
        }
    }

    public static HashMap getMessageGatewayMap() {
        return _messageGatewayMap;
    }

}
