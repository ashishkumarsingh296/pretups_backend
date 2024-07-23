package com.btsl.user.businesslogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.util.BTSLUtil;



public class LocaleMasterCache implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleMasterCache.class);
    private static LocaleMasterCustomeRepository localeMasterCustomeRepository;

    private static HashMap localeMasterMap = new HashMap();
    private static HashMap localeMasterDetailsMap = new HashMap();
    private static final int THREAD_SLEEP_TIME = 50;

    static {
        if (localeMasterCustomeRepository == null) {
            localeMasterCustomeRepository = (LocaleMasterCustomeRepository) com.btsl.common.ApplicationContextProvider
                    .getApplicationContext("TEST").getBean(LocaleMasterCustomeRepository.class);
        }
    }

    public void run() {
        try {
            Thread.sleep(THREAD_SLEEP_TIME);
            refreshLocaleMasterCache();
            MessagesCaches.load(LocaleMasterCache.getLocaleList());
        } catch (InterruptedException e) {
            LOGGER.error("LocaleMasterCache init() Exception ", e);
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void refreshLocaleMasterCache() {
        final String METHOD_NAME = "refreshLocaleMasterCache";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("refreshLocaleMasterCache", "Entered: ");
        }
        try {
            HashMap tempMap = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("refreshLocaleMasterCache", " Before loading:" + localeMasterMap);
            }
            tempMap = localeMasterCustomeRepository.loadLocaleMasterCache();
            localeMasterDetailsMap = localeMasterCustomeRepository.loadLocaleDetailsAtStartUp();
            compareMaps(localeMasterDetailsMap, tempMap);
            localeMasterMap = tempMap;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("refreshLocaleMasterCache", " After loading:" + localeMasterMap.size());
            }
        } catch (Exception e) {
            LOGGER.error("refreshLocaleMasterCache", "Exception " + e.getMessage());
            LOGGER.trace(METHOD_NAME, e);
        }
    }

    /**
     * To compare Maps
     * 
     * @param previousMap
     * @param currentMap
     */
    private static void compareMaps(HashMap previousMap, HashMap currentMap) {
        final String METHOD_NAME = "compareMaps";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("compareMaps()", "Entered PreviousMap " + previousMap + "  Current Map" + currentMap);
        }
        try {
            Iterator iterator = null;
            Iterator copiedIterator = null;
            if (previousMap.size() == currentMap.size()) {
                iterator = previousMap.keySet().iterator();
                copiedIterator = previousMap.keySet().iterator();
            } else if (previousMap.size() > currentMap.size()) {
                iterator = previousMap.keySet().iterator();
                copiedIterator = previousMap.keySet().iterator();
            } else if (previousMap.size() < currentMap.size()) {
                iterator = currentMap.keySet().iterator();
                copiedIterator = previousMap.keySet().iterator();
            }
            boolean isNewAdded = checkNewlyAdded( iterator, previousMap,  currentMap);
            if (previousMap.size() == currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }
                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    Locale mappingVO = (Locale) currentMap.get(iterator2.next());
                    CacheOperationLog.log("LocaleMasterCache",
                    		BTSLUtil.formatMessage("Add", (String) iterator2.next(), mappingVO.toString()));
                }
            }
        } catch (Exception e) {
            LOGGER.trace(METHOD_NAME, e);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("compareMaps()", "Exited");
        }
    }
    
    
    private static boolean  checkNewlyAdded(Iterator iterator,HashMap previousMap, HashMap currentMap) {
    	   boolean isNewAdded = false;
    	 while (iterator != null && iterator.hasNext()) {
             String key = (String) iterator.next();
             Locale prevValue = (Locale) previousMap.get(key);
             Locale currValue = (Locale) currentMap.get(key);

             if (prevValue != null && currValue == null) {
                 isNewAdded = true;
                 CacheOperationLog.log("LocaleMasterCache",
                         BTSLUtil.formatMessage("Delete", key, prevValue.toString()));
             } else if (prevValue == null && currValue != null) {
                 CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Add", key, currValue.toString()));
             } else if (prevValue != null) {
                 if (!currValue.equals(prevValue)) {
                     CacheOperationLog.log("LocaleMasterCache",
                    		 BTSLUtil.formatMessage("Modify", key, "From :" + prevValue + " To:" + currValue));
                 }
             }
         }
    	 return isNewAdded;

    }
    
    

    /**
     * Method to get the Locale from cache
     * 
     * @param p_key
     * @return Locale
     */
    public static Locale getLocaleFromCodeDetails(String p_key) {
        return (Locale) localeMasterMap.get(p_key);
    }

    /**
     * Method to get the Locale details from cache ChangeID=LOCALEMASTER
     * 
     * @param p_key
     * @return LocaleMasterVO
     */
    public static LocaleMasterModal getLocaleDetailsFromlocale(Locale p_key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getLocaleDetailsFromlocale()", "Entered p_key=" + p_key.toString());
        }
        return (LocaleMasterModal) localeMasterDetailsMap.get(p_key);
    }

    /**
     * Method to get the Locales applicable for SMS. ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static List getLocaleListForSMS() {
        ArrayList list = new ArrayList();
        if (localeMasterDetailsMap != null && localeMasterDetailsMap.size() > 0) {
            Iterator iterator = localeMasterDetailsMap.keySet().iterator();
            LocaleMasterModal localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterModal) localeMasterDetailsMap.get(key);
                if (PretupsI.SMS_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
                    list.add(key);
                }
            }
        }
        return list;
    }

    /**
     * Method to get the Locales applicable for WEB. ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static List getLocaleListForWEB() {
        ArrayList list = new ArrayList();
        ArrayList templist = new ArrayList();
        if (localeMasterDetailsMap != null && localeMasterDetailsMap.size() > 0) {
            Iterator iterator = localeMasterDetailsMap.keySet().iterator();
            LocaleMasterModal localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterModal) localeMasterDetailsMap.get(key);
                if (PretupsI.WEB_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
                    templist.add(localeVO);
                }
            }
            Collections.sort(templist);
            for (int i = 0, j = templist.size(); i < j; i++) {
                localeVO = (LocaleMasterModal) templist.get(i);
                list.add(new Locale(localeVO.getLanugage(), localeVO.getCountry()));
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
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static List getLocaleList() {
        ArrayList list = new ArrayList();
        if (localeMasterDetailsMap != null && localeMasterDetailsMap.size() > 0) {
            Iterator iterator = localeMasterDetailsMap.keySet().iterator();
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                list.add(key);
            }
        }
        return list;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static List getLocaleListForALL() {
        ArrayList list = new ArrayList();
        ArrayList templist = new ArrayList();
        if (localeMasterDetailsMap != null && localeMasterDetailsMap.size() > 0) {
            Iterator iterator = localeMasterDetailsMap.keySet().iterator();
            LocaleMasterModal localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterModal) localeMasterDetailsMap.get(key);
                if (PretupsI.SMS_LOCALE.equals(localeVO.getType()) || PretupsI.WEB_LOCALE.equals(localeVO.getType())
                        || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
                    templist.add(localeVO);
                }
            }
            Collections.sort(templist);
            for (int i = 0, j = templist.size(); i < j; i++) {
                localeVO = (LocaleMasterModal) templist.get(i);
                list.add(new Locale(localeVO.getLanugage(), localeVO.getCountry()));
            }
        }
        return list;
    }

}
