package com.btsl.util;

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

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.messages.businesslogic.MessagesDAO;

public class MessagesCache {

	private static Log log = LogFactory.getLog(MessagesCache.class.getName());
	private ResourceBundle bundle;
	private Locale locale = null;
	private Map<String, String> messagesDB = null;

	public MessagesCache(Locale locale) {
		this.locale = locale;
		bundle = ResourceBundle
				.getBundle("configfiles.Messages", locale, new ResourceLoader(getClass()
						.getClassLoader()));
		messagesDB = dbMessages(locale);
	}

	/**
	 * This method fetch the data from the database and create
	 * Map<String,String> based on the Locale
	 * 
	 * @param locale
	 * @return Map<String,String>
	 */
	public static Map<String, String> dbMessages(Locale p_locale) {
		final String methodName = "dbMessages";
		LogFactory.printLog(methodName, "Entered: " + p_locale.getLanguage(), log);

		MessagesDAO _messagesDAO = null;
		Map<String, Object> _messagesMap = null;
		Map<String, String> hashMap = null;
		try {

			_messagesMap = new HashMap<String, Object>();
			_messagesDAO = new MessagesDAO();

			final String localLng = p_locale.getLanguage();

			_messagesMap = _messagesDAO.loadMessageByLocale(localLng);

			LogFactory.printLog(methodName, "DB messagesMap size=: " + _messagesMap.size(), log);
			final Iterator<String> iterator = _messagesMap.keySet().iterator();

			hashMap = new HashMap<String, String>();
			while (iterator.hasNext()) {

				final String temp = iterator.next();

				if (localLng != null && localLng.length() > 0 && localLng
						.equalsIgnoreCase(temp))
					hashMap = (HashMap) _messagesMap.get(temp);
			}

			LogFactory.printLog(methodName, "exited with db messages size=" + _messagesMap.size(), log);

		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
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
		final String methodName = "getProperty";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered: key is = "+key);
        }
		String str = null;
		try {
			String convertedByte;
			String msgDB;
			// defect : messages from db were not checked when message.prop file
			// was empty
			// enhancement: message to be picked from db first and then from
			// file

			msgDB = messagesDB.get(key);
			if (!BTSLUtil.isNullString(msgDB))
			{
				str = msgDB;
				if (locale == null) {
					return str;
				}

				if(!locale.getLanguage().equalsIgnoreCase("ar")  && !locale.getLanguage().equalsIgnoreCase("fa"))
				{
					// ChangeID=LOCALEMASTER
					// charset to be used will be picked from the locale master cache
					final LocaleMasterVO localeVO = LocaleMasterCache
							.getLocaleDetailsFromlocale(locale);
					convertedByte = new String(str.getBytes(), localeVO.getCharset());

					str = convertedByte;
				}
			}
			else
			{
				str = bundle.getString(key);
				if (locale == null) {
					return str;
				}
				// ChangeID=LOCALEMASTER
				// charset to be used will be picked from the locale master cache
				if(!locale.getLanguage().equalsIgnoreCase("ar")  && !locale.getLanguage().equalsIgnoreCase("fa"))
				{
					
				final LocaleMasterVO localeVO = LocaleMasterCache
						.getLocaleDetailsFromlocale(locale);
				convertedByte = new String(str.getBytes(), localeVO.getCharset());
				str = convertedByte;
				}
			}


			LogFactory.printLog(methodName, "Bundle messages =: " + str, log);

		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting with exception for Missing Message Key ::" + key);
				log.errorTrace(methodName, ex);
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
