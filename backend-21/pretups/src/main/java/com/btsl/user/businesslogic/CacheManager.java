/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law.
 * 
 * Comviva reserves all rights not expressly granted. You may not reverse engineer, 
 * decompile, or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * 
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

package com.btsl.user.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btsl.common.ApplicationContextProvider;


/**
 * Title: Caching Description: Copyright: Copyright (c) 2001 Company: Comviva
 * Filename: CacheManager.java
 * 
 * @author Subesh KCV
 * @version 1.0
 */
public class CacheManager {

	/**
	 * 
	 * The Constant LOGGER.
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);

	private static Map<Object, Object> cacheHashMap = new HashMap<>();
	private static VMSCacheRepository vmsCacheRepository;

	private CacheManager() {
	}

	static {
		try {
			/*
			 * Create background thread, which will be responsible for purging expired
			 * items.
			 */
			ThreadCacheManagerCleanup threadCleanerRunnable = new ThreadCacheManagerCleanup();
			Thread threadCleanerUpper = new Thread(threadCleanerRunnable);
			threadCleanerUpper.start();
			threadCleanerUpper.setPriority(Thread.MIN_PRIORITY);
			vmsCacheRepository = (VMSCacheRepository) ApplicationContextProvider.getApplicationContext("TEST")
                    .getBean(VMSCacheRepository.class);


		} catch (ApplicationException e) {
			LOGGER.debug("Exception occoured while CacheManager cleanup process: ", e);
		}

	}

	public static Map<Object, Object> getCacheHashMap() {
		return cacheHashMap;
	}

	/**
	 * Adding the cache by PutCache
	 * 
	 * @param object - input
	 * 
	 */
	public static void putCache(Cacheable object) {
		cacheHashMap.put(object.getIdentifier(), object);
	}

	/**
	 * Get the Language code
	 *
	 * @param lang - input
	 * @return int
	 */
	public static int getLanguageCode(String lang) {
		int langCode=0;
		List<LocaleMasterModal> listLocalMaster =vmsCacheRepository.loadLocaleMaster();		
		for(LocaleMasterModal localMaster : listLocalMaster) {
			 if(localMaster.getLanugage().trim().equals(lang)) {
				 langCode=localMaster.getLanguageCode();
				 break;
			 }	 
				 
		}
		return langCode;
	}

	/**
	 * Get the language Locale
	 *
	 * @param lang - input
	 * @return Locale
	 */
	public static Locale getLanguageLocale(Integer lang) {
		Locale locale=null;
		List<LocaleMasterModal> listLocalMaster =vmsCacheRepository.loadLocaleMaster();		
		for(LocaleMasterModal localMaster : listLocalMaster) {
			 if(localMaster.getLanguageCode().equals(lang)) {
				 //locale = new Locale(localMaster.getLanugage());
				 locale = Locale.forLanguageTag(localMaster.getLanugage());
				 break;
			 }	 
		}
		
		if(null==locale) {
			locale = new Locale(Constants.ENGLISH.getStrValue());
		}
		return locale;
	}

	/**
	 * Get the cache Object
	 *
	 * @param identifier - input
	 * 
	 * @return Cacheable
	 */
	public static Cacheable getCache(Object identifier) {
		Cacheable object = (Cacheable) cacheHashMap.get(identifier);
		if (object == null) {
			return null;
		} else {
			return object;
		}
	}
	
}
