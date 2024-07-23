/**
 * @(#)TransferRulesCache.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             <description>
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan June 30, 2005 Initital Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.selftopup.pretups.transfer.businesslogic;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class TransferRulesCache {
    private static Log _log = LogFactory.getLog(TransferRulesCache.class.getName());

    private static HashMap _txfrRuleMap = new HashMap();
    private static HashMap _prmtrfRuleMap = new HashMap();

    public static void loadTransferRulesAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadTransferRulesAtStartup()", "entered");

        _txfrRuleMap = loadMapping();
        _prmtrfRuleMap = loadPromotionalMapping();

        if (_log.isDebugEnabled())
            _log.debug("loadTransferRulesAtStartup()", "exited");

    }

    /**
     * To load the MSISDN Prefixes and Interfaces Mapping details
     * 
     * @return
     *         HashMap
     */
    private static HashMap loadMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadMapping()", "entered");

        TransferDAO transferDAO = new TransferDAO();
        HashMap map = null;
        try {
            map = transferDAO.loadTransferRuleCache();

        } catch (Exception e) {
            _log.error("loadMapping()", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadMapping()", "exited");
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     */
    public static void updateTransferRulesMapping() {

        if (_log.isDebugEnabled())
            _log.debug("updateTransferRulesMapping()", " Entered");

        HashMap currentMap = loadMapping();

        if (_txfrRuleMap != null && _txfrRuleMap.size() > 0) {
            compareMaps(_txfrRuleMap, currentMap);
        }

        _txfrRuleMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateTransferRulesMapping()", "exited " + _txfrRuleMap.size());

    }

    /**
     * 
     * @param p_module
     * @param p_networkcode
     * @param p_senderSubscriberType
     * @param p_receiverSubscriberType
     * @param p_senderServiceClassID
     * @param p_receiverServiceClassID
     * @return TransferRulesVO
     */
    public static Object getObject(String p_serviceType, String p_module, String p_networkcode, String p_senderSubscriberType, String p_receiverSubscriberType, String p_senderServiceClassID, String p_receiverServiceClassID, String p_subServiceType, String p_rule_level) {
        TransferRulesVO mappingVO = null;

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered  p_serviceType=" + p_serviceType + " p_module: " + p_module + " p_senderSubscriberType: " + p_senderSubscriberType + " receiverSubscriberType " + p_receiverSubscriberType + " p_senderServiceClassID: " + p_senderServiceClassID + " p_receiverServiceClassID: " + p_receiverServiceClassID + " p_subServiceType: " + p_subServiceType + " p_rule_level" + p_rule_level);
        }

        String key = p_serviceType + "_" + p_module + "_" + p_networkcode + "_" + p_senderSubscriberType + "_" + p_receiverSubscriberType + "_" + p_senderServiceClassID + "_" + p_receiverServiceClassID + "_" + p_subServiceType + "_" + p_rule_level;
        mappingVO = (TransferRulesVO) _txfrRuleMap.get(key);

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Exited " + mappingVO);

        return mappingVO;
    }

    /**
     * 
     * @param p_module
     * @param p_networkcode
     * @param p_senderSubscriberType
     * @param p_receiverSubscriberType
     * @param p_senderServiceClassID
     * @param p_receiverServiceClassID
     * @return TransferRulesVO
     */
    public static Object getObject(String p_serviceType, String p_module, String p_networkcode, String p_senderSubscriberType, String p_receiverSubscriberType, String p_senderServiceClassID, String p_receiverServiceClassID, String p_subServiceType) {
        TransferRulesVO mappingVO = null;

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered  p_serviceType=" + p_serviceType + " p_module: " + p_module + " p_senderSubscriberType: " + p_senderSubscriberType + " receiverSubscriberType " + p_receiverSubscriberType + " p_senderServiceClassID: " + p_senderServiceClassID + " p_receiverServiceClassID: " + p_receiverServiceClassID + " p_subServiceType: " + p_subServiceType);
        }

        String key = p_serviceType + "_" + p_module + "_" + p_networkcode + "_" + p_senderSubscriberType + "_" + p_receiverSubscriberType + "_" + p_senderServiceClassID + "_" + p_receiverServiceClassID + "_" + p_subServiceType;
        mappingVO = (TransferRulesVO) _txfrRuleMap.get(key);

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "Exited " + mappingVO);

        return mappingVO;
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

        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
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

            // to check whether any new network added or not but size of
            boolean isNewAdded = false;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                TransferRulesVO prevTransferRulesVO = (TransferRulesVO) p_previousMap.get(key);
                TransferRulesVO curTransferRulesVO = (TransferRulesVO) p_currentMap.get(key);

                if (prevTransferRulesVO != null && curTransferRulesVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    isNewAdded = true;
                    CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Delete", prevTransferRulesVO.getKey(), prevTransferRulesVO.logInfo()));
                } else if (prevTransferRulesVO == null && curTransferRulesVO != null) {
                    // new network added
                    CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Add", curTransferRulesVO.getKey(), curTransferRulesVO.logInfo()));
                } else if (prevTransferRulesVO != null && curTransferRulesVO != null) {
                    if (!curTransferRulesVO.equals(prevTransferRulesVO)) {
                        CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Modify", curTransferRulesVO.getKey(), curTransferRulesVO.differences(prevTransferRulesVO)));

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
                    TransferRulesVO transferRulesVO = (TransferRulesVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("TransferRulesCache", BTSLUtil.formatMessage("Add", transferRulesVO.getKey(), transferRulesVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }

    /**
     * @param p_date
     * @return
     *         integrated from PreTUPS Tigo Guatemala code by rahul
     *         this method loads promotional transfer rule from transfer rule
     *         cache(_txfrRuleMap)
     *         this saves DB hit while doing a transaction
     */
    public static HashMap loadPromotionalTransferRuleCache(Date p_date) {
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalTransferRuleCache", "entered" + "p_date ::" + p_date);
        TransferRulesVO transverVO = null;
        HashMap map = new HashMap();
        boolean isExist = false;
        try {
            // Iterator itr = _txfrRuleMap.keySet().iterator();
            Iterator itr = _prmtrfRuleMap.keySet().iterator();
            while (itr.hasNext()) {
                // transverVO=(TransferRulesVO)_txfrRuleMap.get( itr.next());
                transverVO = (TransferRulesVO) _prmtrfRuleMap.get(itr.next());
                if (!BTSLUtil.isNullString(transverVO.getRuleType()) && transverVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                    {
                        // check if promotional transfer rule is in range then
                        // add to map
                        isExist = PretupsBL.isPromotionalRuleExistInRange(transverVO, p_date);
                        if (isExist)
                            map.put(transverVO.getKey(), transverVO);
                        else
                            continue;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("loadPromotionalTransferRuleCache", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalTransferRuleCache", "exited map size " + map.size());
        return map;
    }

    /**
     * To load the MSISDN Prefixes and Interfaces Mapping details
     * 
     * @return
     *         HashMap
     *         Gaurav pandey
     */
    private static HashMap loadPromotionalMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalMapping()", "entered");

        TransferDAO transferDAO = new TransferDAO();
        HashMap map = null;
        try {
            map = transferDAO.loadPromotionalTransferRuleCache();

        } catch (Exception e) {
            _log.error("loadPromotionalMapping()", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalMapping()", "exited");
        return map;
    }

}
