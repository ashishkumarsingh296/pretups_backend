package com.btsl.user.businesslogic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btsl.util.BTSLUtil;



public class MessagesCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesCache.class);
    private ResourceBundle bundle;
    private Locale locale = null;
    private Map<String, String> messagesDB = null;

    private static MessagesCustomRepository messagesCustomRepository;

    
    static {
        if (messagesCustomRepository == null) {
            messagesCustomRepository = (MessagesCustomRepository) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                    .getBean(MessagesCustomRepository.class);
        }
    }

    public MessagesCache(Locale locale) {
        this.locale = locale;
        bundle = ResourceBundle.getBundle("configfiles.Messages", locale,
                new ResourceLoader(getClass().getClassLoader()));
        messagesDB = dbMessages(locale);
    }

    /**
     * This method fetch the data from the database and create
     * Map<String,String> based on the Locale
     * 
     * @param locale
     * @return Map<String,String>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> dbMessages(Locale plocale) {
        final String methodName = "dbMessages";

        Map<String, Object> messagesMap = null;
        Map<String, String> hashMap = null;
        try {
            messagesMap = new HashMap<String, Object>();
            final String localLng = plocale.getLanguage();
            messagesMap = messagesCustomRepository.loadMessageByLocale(localLng);
            final Iterator<String> iterator = messagesMap.keySet().iterator();
            hashMap = new HashMap<String, String>();
            while (iterator.hasNext()) {
                final String temp = iterator.next();
                if (localLng != null && localLng.length() > 0 && localLng.equalsIgnoreCase(temp))
                    hashMap = (HashMap) messagesMap.get(temp);
            }

        } catch (VMSBaseException e) {
            LOGGER.error(methodName, "Exception " + e.getMessage());
            LOGGER.trace(methodName, e);
        }
        return hashMap;
    }

    /**
     * Get a string from the underlying resource bundle. This methos is changed
     * by ankit zindal on date 25/07/06 for taking character set from system
     * preferences Also encoding is removed from the getBytes method.
     * 
     * @param key
     */
    public String getProperty(String key) {
        final String methodName = "getProperty";
        LOGGER.debug( methodName, "Entered: key is = " + key);
        String str = null;
        try {
            String convertedByte;
            String msgDB;
            msgDB = messagesDB.get(key);
            if (!BTSLUtil.isNullString(msgDB)) {
                str = msgDB;
                if (CommonUtils.isNullorEmpty(locale)) {
                    return str;
                }
                if (!"ar".equalsIgnoreCase(locale.getLanguage()) && !"fa".equalsIgnoreCase(locale.getLanguage())) {
                    final LocaleMasterModal localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    convertedByte = new String(str.getBytes(), localeVO.getCharset());
                    str = convertedByte;
                }
            } else {
                str = bundle.getString(key);
                if (CommonUtils.isNullorEmpty(locale)) {
                    return str;
                }
                if (!"ar".equalsIgnoreCase(locale.getLanguage()) && !"fa".equalsIgnoreCase(locale.getLanguage())) {
                    final LocaleMasterModal localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    convertedByte = new String(str.getBytes(), localeVO.getCharset());
                    str = convertedByte;
                }
            }
        } catch (Exception ex) {
        	LOGGER.debug( methodName, "Exiting with exception for Missing Message Key ::" + key);
        	LOGGER.trace(methodName, ex);
        }
        return str;
    }

    private static class ResourceLoader extends ClassLoader {
        public ResourceLoader(ClassLoader parent) {
            super(parent);
        }
    }
}
