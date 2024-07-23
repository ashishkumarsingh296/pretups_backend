package com.btsl.user.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btsl.pretups.common.PretupsI;

public class MessagesCaches implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesCaches.class);
    private static HashMap map = new HashMap();
    private static final int THREAD_SLEEP_TIME = 50;

    public void run() {
        try {
            Thread.sleep(THREAD_SLEEP_TIME);
            load(LocaleMasterCache.getLocaleList());
        } catch (InterruptedException e) {
            LOGGER.error("MessagesCaches init() Exception ", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Get a string from the underlying resource bundle.
     * 
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static void load(List localeList) {
        final String methodName = "load";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(methodName, PretupsI.ENTERED);
        }
        for (int i = 0, j = localeList.size(); i < j; i++) {
            try {
                map.put(localeList.get(i), new MessagesCache((Locale) localeList.get(i)));
            } catch (Exception e) {
                LOGGER.error(methodName, PretupsI.EXCEPTION + e.getMessage());
                LOGGER.trace(methodName, e);
            } finally {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(methodName, PretupsI.EXITED);
                }
            }
        }
    }

    public static void reload(List localeList) {
        final String methodName = "reload";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(methodName, PretupsI.ENTERED);
        }
        try {
            load(localeList);
        } catch (Exception e) {
            LOGGER.error(methodName, PretupsI.EXCEPTION + e.getMessage());
            LOGGER.trace(methodName, e);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(methodName, PretupsI.EXITED);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public static MessagesCache get(Object plocale) {
        LOGGER.debug("get", "plocale: " + plocale);
        return (MessagesCache) map.get(plocale);
    }
}
