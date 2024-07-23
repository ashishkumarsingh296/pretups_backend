package com.selftopup.util;

/**
 * @(#)MessagesCache.java
 *                        Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        This class is used to store System Preferences for
 *                        Pretups System.
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Abhijit Chauhan June 10,2005 Initial Creation
 *                        Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *                        Chhaya Sikheria Sep 29,2011
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterVO;
import com.selftopup.pretups.messages.businesslogic.MessagesDAO;

public class MessagesCache {

    private static Log _log = LogFactory.getLog(MessagesCache.class.getName());
    private ResourceBundle _bundle;
    private Locale _locale = null;
    private Map<String, String> _messagesDB = null;

    public MessagesCache(Locale locale) {
        _locale = locale;
        _bundle = ResourceBundle.getBundle("configfiles.selftopup.Messages", locale, new ResourceLoader(getClass().getClassLoader()));
        _messagesDB = dbMessages(locale);
    }

    /**
     * This method fetch the data from the database and create
     * Map<String,String> based on the Locale
     * 
     * @param locale
     * @return Map<String,String>
     */
    public static Map<String, String> dbMessages(Locale p_locale) {
        if (_log.isDebugEnabled()) {
            _log.debug("dbMessages", "Entered: " + p_locale.getLanguage());
        }
        MessagesDAO _messagesDAO = null;
        Map<String, Object> _messagesMap = null;
        Map<String, String> hashMap = null;
        try {

            _messagesMap = new HashMap<String, Object>();
            _messagesDAO = new MessagesDAO();

            String localLng = p_locale.getLanguage();

            _messagesMap = _messagesDAO.loadMessageByLocale(localLng);

            if (_log.isDebugEnabled()) {
                _log.debug("dbMessages", "DB messagesMap size=: " + _messagesMap.size());
            }
            Iterator<String> iterator = _messagesMap.keySet().iterator();

            hashMap = new HashMap<String, String>();
            while (iterator.hasNext()) {

                String temp = (String) iterator.next();

                if (localLng != null && localLng.length() > 0) {
                    if (localLng.equalsIgnoreCase(temp)) {
                        hashMap = (HashMap) _messagesMap.get(temp);
                    }
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug("dbMessages", "exited with db messages size=" + _messagesMap.size());
            }
        } catch (Exception e) {
            // TODO: handle exception
            _log.error("dbMessages", "Exception " + e.getMessage());
            e.printStackTrace();
        }

        return hashMap;

    }

    /**
     * Get a string from the underlying resource bundle.
     * This methos is changed by ankit zindal on date 25/07/06 for taking
     * character set from system preferences
     * Also encoding is removed from the getBytes method.
     * 
     * @param key
     */
    public String getProperty(String key) {
        String str = null;
        try {
            String convertedByte = null;
            str = _bundle.getString(key);
            if (_locale == null)
                return str;
            // ChangeID=LOCALEMASTER
            // charset to be used will be picked from the locale master cache
            LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
            convertedByte = new String(str.getBytes(), localeVO.getCharset());

            /*
             * else
             * if(_locale.getLanguage().equals(PretupsI.LOCALE_LANGAUGE_EN))
             * convertedByte= new String(str.getBytes(),"UTF-8");
             * else
             * {
             * String encodingToUse=SystemPreferences.SECOND_LANGUAGE_CHARSET;
             * //If Not defined then use the default encoding
             * if(BTSLUtil.isNullString(encodingToUse))
             * encodingToUse="UTF-8";
             * 
             * if(_locale.getLanguage().equals("fr"))
             * convertedByte= new String(str.getBytes(),encodingToUse);
             * else
             * convertedByte= new String(str.getBytes(),encodingToUse);
             * }
             */
            str = convertedByte;

            if (_log.isDebugEnabled()) {
                _log.debug("getProperty", "Bundle messages =: " + str);
            }
            // Override the Message from DB based on key
            if (_messagesDB != null && _messagesDB.size() > 0) {

                String msgDB = (String) _messagesDB.get(key);
                if (!BTSLUtil.isNullString(msgDB))
                    str = msgDB;

                if (_log.isDebugEnabled()) {
                    _log.debug("getProperty", "DB messages =: " + str);
                }
            }
        } catch (Exception ex) {
            if (_log.isDebugEnabled()) {
                _log.debug("getProperty", "Exiting with exception for Missing Message Key ::" + key);
                ex.printStackTrace();
            }
        }
        return str;
    }

    private static class ResourceLoader extends ClassLoader {
        public ResourceLoader(ClassLoader parent) {
            super(parent);
        }
    }
}
