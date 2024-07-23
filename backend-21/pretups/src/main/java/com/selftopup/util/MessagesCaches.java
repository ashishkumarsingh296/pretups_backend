package com.selftopup.util;

/**
 * @(#)MessagesCaches.java
 *                         Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         This class is used to store System Preferences for
 *                         Pretups System.
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Abhijit Chauhan June 10,2005 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class MessagesCaches {

    private static Log _log = LogFactory.getLog(MessagesCaches.class.getName());
    private static HashMap _map = new HashMap();

    /**
     * Get a string from the underlying resource bundle.
     * 
     * @param key
     */
    public static void load(ArrayList localeList) {
        for (int i = 0, j = localeList.size(); i < j; i++)
            try {
                _map.put(localeList.get(i), new MessagesCache((Locale) localeList.get(i)));
            } catch (Exception e) {
                _log.error("load", "Exception in getting Bundle:" + e.getMessage());
            }
    }

    public static void reload(ArrayList localeList) {
        load(localeList);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public static MessagesCache get(Object p_locale) {
        _log.debug("get", "p_locale: " + p_locale);
        // TODO Auto-generated method stub
        return (MessagesCache) _map.get(p_locale);
    }
}
