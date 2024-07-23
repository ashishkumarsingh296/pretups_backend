package com.selftopup.pretups.master.businesslogic;

/*
 * @(#)LocaleMasterCache.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gurjeet Singh Nov 04, 2005 Initital Creation
 * Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 * ------------------------------------------------------------------------------
 * -------------------
 * Cache for locale
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.CacheOperationLog;
import com.selftopup.util.BTSLUtil;

public class LocaleMasterCache {
    private static Log _log = LogFactory.getLog(LocaleMasterCache.class.getName());
    private static HashMap _localeMasterMap = new HashMap();
    private static HashMap _localeMasterDetailsMap = new HashMap();
    private static LocaleMasterDAO _localeMasterDAO = new LocaleMasterDAO();

    public static void refreshLocaleMasterCache() {
        if (_log.isDebugEnabled())
            _log.debug("refreshLocaleMasterCache", "Entered: ");
        try {
            HashMap tempMap = null;
            // String loadType=null; Not used so comment by ankit Jindal
            if (_log.isDebugEnabled())
                _log.debug("refreshLocaleMasterCache", " Before loading:" + _localeMasterMap);
            tempMap = _localeMasterDAO.loadLocaleMasterCache();
            _localeMasterDetailsMap = _localeMasterDAO.loadLocaleDetailsAtStartUp();
            compareMaps(_localeMasterMap, tempMap);
            _localeMasterMap = tempMap;
            if (_log.isDebugEnabled())
                _log.debug("refreshLocaleMasterCache", " After loading:" + _localeMasterMap.size());
        } catch (Exception e) {
            _log.error("refreshLocaleMasterCache", "Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * To compare Maps
     * 
     * @param p_previousMap
     * @param p_currentMap
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
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
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Locale prevValue = (Locale) p_previousMap.get(key);
                Locale currValue = (Locale) p_currentMap.get(key);

                if (prevValue != null && currValue == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Delete", key, prevValue.toString()));
                } else if (prevValue == null && currValue != null)
                    CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Add", key, currValue.toString()));
                else if (prevValue != null && currValue != null) {
                    if (!currValue.equals(prevValue))
                        CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Modify", key, "From :" + prevValue + " To:" + currValue));
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
                    Locale mappingVO = (Locale) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Add", (String) iterator2.next(), mappingVO.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("compareMaps()", "Exited");
    }

    /**
     * Method to get the Locale from cache
     * 
     * @param p_key
     * @return Locale
     */
    public static Locale getLocaleFromCodeDetails(String p_key) {
        return (Locale) _localeMasterMap.get(p_key);
    }

    /**
     * Method to get the Locale details from cache
     * ChangeID=LOCALEMASTER
     * 
     * @param p_key
     * @return LocaleMasterVO
     */
    public static LocaleMasterVO getLocaleDetailsFromlocale(Locale p_key) {
        if (_log.isDebugEnabled())
            _log.debug("getLocaleDetailsFromlocale()", "Entered p_key=" + p_key.toString());
        return (LocaleMasterVO) _localeMasterDetailsMap.get(p_key);
    }

    /**
     * Method to get the Locales applicable for SMS.
     * ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    public static ArrayList getLocaleListForSMS() {
        ArrayList list = new ArrayList();
        if (_localeMasterDetailsMap != null && _localeMasterDetailsMap.size() > 0) {
            Iterator iterator = _localeMasterDetailsMap.keySet().iterator();
            LocaleMasterVO localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterVO) _localeMasterDetailsMap.get(key);
                if (PretupsI.SMS_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType()))
                    list.add(key);
            }
        }
        return list;
    }

    /**
     * Method to get the Locales applicable for WEB.
     * ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    public static ArrayList getLocaleListForWEB() {
        ArrayList list = new ArrayList();
        ArrayList templist = new ArrayList();
        if (_localeMasterDetailsMap != null && _localeMasterDetailsMap.size() > 0) {
            Iterator iterator = _localeMasterDetailsMap.keySet().iterator();
            LocaleMasterVO localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterVO) _localeMasterDetailsMap.get(key);
                if (PretupsI.WEB_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType()))
                    templist.add(localeVO);
            }
            Collections.sort(templist);
            for (int i = 0, j = templist.size(); i < j; i++) {
                localeVO = (LocaleMasterVO) templist.get(i);
                list.add(new Locale(localeVO.getLanguage(), localeVO.getCountry()));
            }
        }
        return list;
    }

    /**
     * Method to get the Locales applicable for WEB,SMS and BOTH.
     * ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    public static ArrayList getLocaleList() {
        ArrayList list = new ArrayList();
        if (_localeMasterDetailsMap != null && _localeMasterDetailsMap.size() > 0) {
            Iterator iterator = _localeMasterDetailsMap.keySet().iterator();
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                list.add(key);
            }
        }
        return list;
    }

}
