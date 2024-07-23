/**
 * @(#)LookupsCache.java
 *                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       <description>
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       avinash.kamthan Mar 13, 2005 Initital Creation
 *                       Ankit Zindal Aug 10, 2006 Modification for
 *                       ID=LKUPCMPMAP
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 * 
 */

package com.selftopup.pretups.master.businesslogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class LookupsCache {

    private static Log _log = LogFactory.getLog(LookupsCache.class.getName());

    private static HashMap _lookupMap = new HashMap();

    /**
     * To load the lookup at the server startup
     * 
     * void
     * LookupsCache
     */
    public static void loadLookAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadLookups()", "entered");
        _lookupMap = loadLookups();

        if (_log.isDebugEnabled())
            _log.debug("loadLookups()", "exited");
    }

    /**
     * To load the Lookups
     * 
     * @return
     *         HashMap
     *         NetworkCache
     */
    private static HashMap loadLookups() {
        if (_log.isDebugEnabled())
            _log.debug("loadLookups()", "entered");

        LookupsDAO lookupsDAO = new LookupsDAO();
        HashMap map = null;
        try {
            map = lookupsDAO.loadLookups();
        } catch (Exception e) {
            _log.error("loadLookups() ", e);
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadLookups()", "exited");
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * LookupsCache
     */
    public static void updateData() {
        // String method = "updateData()";

        if (_log.isDebugEnabled())
            _log.debug("updateData()", "Entered");

        HashMap currentMap = loadLookups();

        if (_lookupMap != null && _lookupMap.size() > 0) {

            compareMaps(_lookupMap, currentMap);

        }

        _lookupMap = currentMap;

        if (_log.isDebugEnabled())
            _log.debug("updateData()", "exited " + _lookupMap.size());
    }

    /**
     * to load the lookup list against lookupType
     * 
     * @param lookupType
     * @return ArrayList
     */
    public static ArrayList getLookupList(String lookupType) {

        if (_log.isDebugEnabled())
            _log.debug("getLookupList()", "entered");
        ArrayList list = (ArrayList) _lookupMap.get(lookupType);
        Collections.sort(list);
        if (_log.isDebugEnabled())
            _log.debug("getLookupList()", "exited" + list.size());
        return list;
    }

    /**
     * To load the lookups drop down on the bases of lookUp Type
     * To load the active lookups p_active should be true.
     * if we want to load all lookups then pass p_active should be false
     * 
     * @param p_lookupType
     * @param p_active
     * @return
     *         ArrayList
     *         LookupsCache
     */
    public static ArrayList loadLookupDropDown(String p_lookupType, boolean p_active) {

        if (_log.isDebugEnabled())
            _log.debug("getLookupList()", "entered  p_lookupType: " + p_lookupType + " p_active: " + p_active);
        ArrayList lookupList = new ArrayList();
        try {
            ArrayList list = (ArrayList) _lookupMap.get(p_lookupType);

            for (int i = 0; i < list.size(); i++) {
                LookupsVO lookupsVO = (LookupsVO) list.get(i);

                ListValueVO listValueVO = new ListValueVO(lookupsVO.getLookupName(), lookupsVO.getLookupCode());
                listValueVO.setOtherInfo(lookupsVO.getLookupType());
                /*
                 * Note:
                 * to load only the active lookup if p_status is not "All"
                 */

                if (p_active && "Y".equals(lookupsVO.getStatus()))
                    lookupList.add(listValueVO);
                else
                    lookupList.add(listValueVO);
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadLookupDropDown()", "exited" + lookupList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

    /**
     * 
     * @param lookupType
     * @param lookupCode
     * @return Object
     */
    public static Object getObject(String lookupType, String lookupCode) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("getObject()", "entered");
        ArrayList list = (ArrayList) _lookupMap.get(lookupType);
        LookupsVO lookupsVO = null;

        for (int i = 0, k = list.size(); i < k; i++) {

            lookupsVO = (LookupsVO) list.get(i);
            if (lookupCode != null && lookupCode.equals(lookupsVO.getLookupCode())) {
                break;
            } // Method is changed by ankit zindal on date 2/8/06 as logic of
              // searching was wrong
            else
                lookupsVO = null;
        }
        if (lookupsVO == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LookupCache[getObject]", "", "", "", "Exception:Lookup Code not defined Lookup code=" + lookupCode + " Lookup type=" + lookupType);
            throw new BTSLBaseException("LookupCache", "getObject", SelfTopUpErrorCodesI.ERROR_INVALID_LOOKUP_CODE);
        }
        if (_log.isDebugEnabled())
            _log.debug("getObject()", "exited" + list.size());

        return lookupsVO;
    }

    /**
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     * 
     */

    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {

        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Entered p_previousMap " + p_previousMap + "  p_currentMap: " + p_currentMap);
        try {
            Iterator iterator = null;
            if (p_previousMap.size() == p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() > p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() < p_currentMap.size()) {
                iterator = p_currentMap.keySet().iterator();
            }

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                ArrayList prevLooupList = (ArrayList) p_previousMap.get(key);
                ArrayList curLooupList = (ArrayList) p_currentMap.get(key);

                if ((prevLooupList != null && prevLooupList.size() > 0) && (curLooupList != null && curLooupList.size() == 0)) {
                    logData("Deleted", prevLooupList, null);
                } else if ((prevLooupList != null && prevLooupList.size() == 0) && (curLooupList != null && curLooupList.size() > 0)) {
                    logData("Added", curLooupList, null);
                } else if ((prevLooupList != null && prevLooupList.size() > 0) || (curLooupList != null && curLooupList.size() > 0)) {
                    logData("Modified", prevLooupList, curLooupList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "exited");
    }

    /**
     * 
     * @param p_action
     * @param p_primaryList
     * @param p_secondryList
     *            void
     */
    private static void logData(String p_action, ArrayList p_primaryList, ArrayList p_secondryList) {

        if (_log.isDebugEnabled())
            _log.debug("logData()", "Entered  p_action: " + p_action + " p_primaryList: " + p_primaryList + " p_secondryList: " + p_secondryList);
        try {
            if (p_secondryList == null) {
                for (int i = 0, k = p_primaryList.size(); i < k; i++) {
                    LookupsVO lookupsVO = (LookupsVO) p_primaryList.get(i);
                    // System.out.println(p_action+"   "+lookupsVO.getLookupType()+"   "+lookupsVO.logInfo());
                    CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage(p_action, lookupsVO.getLookupType(), lookupsVO.logInfo()));
                }
            } else if (p_primaryList != null)// change is done for
                                             // ID=LKUPCMPMAP. This is done to
                                             // avoid null pointer exception
                                             // when primaryList is null
            // This is done temorarily and will have to be changed in new design
            // of cache.
            {
                ArrayList secondryList = new ArrayList(p_secondryList);
                for (int i = 0, k = p_primaryList.size(); i < k; i++) {
                    LookupsVO primaryLookupsVO = (LookupsVO) p_primaryList.get(i);
                    boolean flag = false;
                    for (int m = 0; m < secondryList.size(); m++) {
                        LookupsVO secondryLookupsVO = (LookupsVO) secondryList.get(m);
                        if (primaryLookupsVO.getLookupType().equals(secondryLookupsVO.getLookupType()) && primaryLookupsVO.getLookupCode().equals(secondryLookupsVO.getLookupCode())) {
                            if (!primaryLookupsVO.equals(secondryLookupsVO)) {
                                // /System.out.println(p_action+"   "+primaryLookupsVO.getLookupType()+"   "+secondryLookupsVO.differences(primaryLookupsVO));
                                CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage(p_action, primaryLookupsVO.getLookupType(), secondryLookupsVO.differences(primaryLookupsVO)));
                            }
                            flag = true;
                            secondryList.remove(m);
                            break;
                        }
                    }

                    if (!flag) {
                        // System.out.println(BTSLUtil.formatMessage("Delete",primaryLookupsVO.getLookupType(),primaryLookupsVO.logInfo()));
                        CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage("Delete", primaryLookupsVO.getLookupType(), primaryLookupsVO.logInfo()));
                    }
                }

                for (int m = 0; m < secondryList.size(); m++) {
                    LookupsVO secondryLookupsVO = (LookupsVO) secondryList.get(m);
                    // System.out.println(BTSLUtil.formatMessage("Add",secondryLookupsVO.getLookupType(),secondryLookupsVO.logInfo()));
                    CacheOperationLog.log("LookupsCache", BTSLUtil.formatMessage("Add", secondryLookupsVO.getLookupType(), secondryLookupsVO.logInfo()));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("logData()", " Exited ");
    }
}
