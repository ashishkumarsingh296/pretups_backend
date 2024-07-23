/**
 * @(#)PreferenceCache.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          <description>
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          avinash.kamthan Mar 16, 2005 Initital Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 * 
 */
package com.selftopup.pretups.preference.businesslogic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

/**
 * @author avinash.kamthan
 */
public class PreferenceCache {
    private static Log _log = LogFactory.getLog(PreferenceCache.class.getName());
    private static HashMap _preferenceMap = new HashMap();

    /**
     * To Load all prefrences on startup Preferences like System level,Network
     * Level,Zone Level,Service Level void PreferenceCache
     */
    public static void loadPrefrencesOnStartUp() {
        if (_log.isDebugEnabled())
            _log.debug("loadPrefrencesOnStartUp()", "Entered");
        _preferenceMap = loadPrefrences();
        if (_log.isDebugEnabled())
            _log.debug("loadPrefrencesOnStartUp()", "Exited");
    }

    /**
     * To load the Preferences
     * 
     * @return HashMap PreferenceCache
     */
    private static HashMap loadPrefrences() {
        if (_log.isDebugEnabled())
            _log.debug("loadPrefrences()", "Entered");
        PreferenceDAO preferenceCacheDAO = new PreferenceDAO();
        HashMap map = null;
        try {
            map = preferenceCacheDAO.loadPrefrences();
        } catch (Exception e) {
            _log.error("loadPrefrences()", "Exception e: " + e);
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadPrefrences()", "Exited " + map.size());
        return map;
    }

    /**
     * To update the preferences cache void PreferenceCache
     */
    public static void updatePrefrences() {
        if (_log.isDebugEnabled())
            _log.debug("updatePrefrences()", " Entered");
        HashMap currentMap = loadPrefrences();
        if ((_preferenceMap != null) && (_preferenceMap.size() > 0)) {
            compareMaps(_preferenceMap, currentMap);
        }
        _preferenceMap = currentMap;
        if (_log.isDebugEnabled())
            _log.debug("updateData()", "Exited " + _preferenceMap.size());
    }

    /**
     * get the value against the prefrenceCode at system level
     * 
     * @param p_preferenceCode
     * @return String PreferenceCache
     */
    public static Object getSystemPreferenceValue(String p_preferenceCode) {
        if (_log.isDebugEnabled())
            _log.debug("getSystemPreferenceValue()", "Entered p_preferenceCode: " + p_preferenceCode);
        Object value = null;
        PreferenceCacheVO cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode);
        if (cacheVO != null)
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        if (_log.isDebugEnabled())
            _log.debug("getSystemPreferenceValue()", "Exited value: " + value);
        return value;
    }

    /**
     * get the value against the prefrenceCode at Network level. if value is not
     * found at network level then search it at System level
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @return String PreferenceCache
     */
    public static Object getNetworkPrefrencesValue(String p_preferenceCode, String p_networkCode) {
        if (_log.isDebugEnabled())
            _log.debug("getNetworkPrefrencesValue()", "Entered p_preferenceCode: " + p_preferenceCode + " networkCode " + p_networkCode);
        PreferenceCacheVO cacheVO = null;
        cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode);
        Object value = null;

        // if prefrence is not found at network level then try to find it at
        // system level
        if (cacheVO == null) {
            value = getSystemPreferenceValue(p_preferenceCode);
        } else {
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        }
        if (_log.isDebugEnabled())
            _log.debug("getNetworkPrefrencesValue()", "Exited value: " + value);
        return value;
    }

    /**
     * get the value against the prefrenceCode at Zone level. if value is not
     * found at network level then search it at System level
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @param p_controlCode
     * @return String PreferenceCache
     */
    public static Object getControlPreference(String p_preferenceCode, String p_networkCode, String p_controlCode) {
        if (_log.isDebugEnabled())
            _log.debug("getControlPreference()", "Entered p_preferenceCode: " + p_preferenceCode + " networkCode " + p_networkCode + " p_controlCode: " + p_controlCode);
        PreferenceCacheVO cacheVO = null;
        cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode + ":" + p_controlCode);
        Object value = null;

        if (cacheVO == null)
            value = getSystemPreferenceValue(p_preferenceCode);
        else
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        if (_log.isDebugEnabled())
            _log.debug("getControlPreference()", "Exited value=" + value);
        return value;
    }

    /**
     * get the value against the prefrenceCode at Service level. if value is not
     * found at network level then search it at System level
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @param p_zoneCode
     * @param p_module
     * @param p_serviceCode
     * @return String PreferenceCache
     */
    public static Object getServicePreference(String p_preferenceCode, String p_networkCode, String p_module, String p_serviceCode) {
        if (_log.isDebugEnabled())
            _log.debug("getServicePreference()", "Entered p_preferenceCode : " + p_preferenceCode + " p_networkCode: " + p_networkCode + "  p_serviceCode: " + p_serviceCode + " p_module: " + p_module);
        Object value = null;
        value = getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceCode, true);
        if (_log.isDebugEnabled())
            _log.debug("getServicePreference()", "Exited value: " + value);
        return value;
    }

    /**
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @param p_module
     * @param p_serviceCode
     * @param systemDefaultValueRequired
     *            true then find the vlaue at System level other wise just check
     *            the value at service level
     * @return
     */
    public static Object getServicePreference(String p_preferenceCode, String p_networkCode, String p_module, String p_serviceCode, boolean systemDefaultValueRequired) {
        if (_log.isDebugEnabled())
            _log.debug("getServicePreference()", "Entered p_preferenceCode: " + p_preferenceCode + " p_networkCode: " + p_networkCode + "  p_serviceCode: " + p_serviceCode + " p_module: " + p_module + " systemDefaultValueRequired: " + systemDefaultValueRequired);
        PreferenceCacheVO cacheVO = null;
        cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode + ":" + p_module + ":" + p_serviceCode);
        Object value = null;
        if (cacheVO == null) {
            if (systemDefaultValueRequired) {
                value = getSystemPreferenceValue(p_preferenceCode);
            }
        } else {
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getServicePreference()", "Exited value: " + value);
        }
        return value;
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
            _log.debug("compareMaps()", "Entered p_previousMap " + p_previousMap + "  p_currentMap: " + p_currentMap);
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
                PreferenceCacheVO prevPreferenceCacheVO = (PreferenceCacheVO) p_previousMap.get(key);
                PreferenceCacheVO curPreferenceCacheVO = (PreferenceCacheVO) p_currentMap.get(key);

                if ((prevPreferenceCacheVO != null) && (curPreferenceCacheVO == null)) {
                    isNewAdded = true;
                    CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Delete", prevPreferenceCacheVO.getPreferenceLevel(), prevPreferenceCacheVO.logInfo()));
                } else if ((prevPreferenceCacheVO == null) && (curPreferenceCacheVO != null)) {
                    CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Add", curPreferenceCacheVO.getPreferenceLevel(), curPreferenceCacheVO.logInfo()));
                } else if ((prevPreferenceCacheVO != null) && (curPreferenceCacheVO != null)) {
                    if (!curPreferenceCacheVO.equals(prevPreferenceCacheVO)) {
                        CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Modify", curPreferenceCacheVO.getPreferenceLevel(), curPreferenceCacheVO.differences(prevPreferenceCacheVO)));
                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted
             * as well
             */
            if ((p_previousMap.size() == p_currentMap.size()) && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);

                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();

                while (iterator2.hasNext()) {
                    // new network added
                    PreferenceCacheVO preferenceCacheVO = (PreferenceCacheVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Add", preferenceCacheVO.getPreferenceLevel(), preferenceCacheVO.logInfo()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }

    /**
     * @param p_objType
     * @param p_objValue
     * @return Object it can be diffrent type as Integer,Long,Boolean,Date,
     *         String PreferenceCache
     */
    private static Object getCastedObject(String p_objType, String p_objValue) {
        Object obj = null;

        if (PreferenceI.TYPE_INTEGER.equals(p_objType)) {
            obj = new Integer(p_objValue);
        } else if (PreferenceI.TYPE_LONG.equals(p_objType)) {
            obj = new Long(p_objValue);
        } else if (PreferenceI.TYPE_BOOLEAN.equals(p_objType)) {
            obj = new Boolean(p_objValue);
        } else if (PreferenceI.TYPE_AMOUNT.equals(p_objType)) {
            obj = new Long(p_objValue);
        } else if (PreferenceI.TYPE_DATE.equals(p_objType)) {
            try {
                obj = BTSLUtil.getDateFromDateString(p_objValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (PreferenceI.TYPE_STRING.equals(p_objType)) {
            obj = p_objValue;
        }

        return obj;
    }

    public static Date getSQLDate(String dateStr) {
        if ((dateStr == null) || dateStr.equals("")) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;

        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new java.sql.Date(date.getTime());
    }

    public static Object getServicePreferenceObject(String p_preferenceCode, String p_networkCode, String p_module, String p_serviceCode, boolean systemDefaultValueRequired) {
        if (_log.isDebugEnabled())
            _log.debug("getServicePreferenceObject()", "Entered p_preferenceCode: " + p_preferenceCode + " p_networkCode: " + p_networkCode + "  p_serviceCode: " + p_serviceCode + " p_module: " + p_module + " systemDefaultValueRequired " + systemDefaultValueRequired);
        PreferenceCacheVO cacheVO = null;
        cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode + ":" + p_module + ":" + p_serviceCode);

        Object value = null;
        if (cacheVO == null)
            cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode);
        if (cacheVO == null)
            cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode);
        if (_log.isDebugEnabled())
            _log.debug("getNetworkPrefrencesValue()", "Exited cacheVO: " + cacheVO);
        return cacheVO;
    }
}
