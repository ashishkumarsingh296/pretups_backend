package common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Loads properties files into HashMap
 */
public class LoadPropertiesFile {
	
	private static Log _log = LogFactory.getFactory().getInstance(
			LoadPropertiesFile.class.getName());
	static final HashMap<String, String> cacheMap = new HashMap<String, String>();

	/**
	 * <h1>HashMap's Getter Method</h1>
	 * 
	 * @return
	 */
	public static HashMap<String, String> getCachemap() {
		loadDataFromPropertyFile();
		loadLocatorReferencesFromPropertyFile();
		return cacheMap;
	}

	/**
	 * <h1>Loads dataFile.properties to HashMap</h1>
	 * 
	 * @return
	 */
	public static HashMap<String, String> loadDataFromPropertyFile() {
		final String file = "dataFile.properties";
		Properties propertyFile = new Properties();
		FileInputStream inputStream = null;
		try {
			File propfile = new File(file);
			inputStream = new FileInputStream(propfile);
			propertyFile.load(inputStream);
		} catch (FileNotFoundException e) {
			_log.error("Exception:" + e);
		} catch (IOException e) {
			_log.error("Exception:" + e);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				_log.error("Exception:" + e);
			}
		}

		Set<Object> keys = propertyFile.keySet();
		for (Object k : keys) {
			String key = (String) k;
			String value = propertyFile.getProperty(key);
			cacheMap.put(key, value);
		}
		return cacheMap;
	}

	/**
	 * <h1>Loads locator.properties to HashMap</h1>
	 * 
	 * @return
	 */
	public static HashMap<String, String> loadLocatorReferencesFromPropertyFile() {
		final String file = "locator.properties";
		Properties propFile1 = new Properties();
		FileInputStream ip1 = null;
		try {
			File propFile = new File(file);
			ip1 = new FileInputStream(propFile);
			propFile1.load(ip1);
		} catch (FileNotFoundException e) {
			_log.error("Exception:" + e);
		} catch (IOException e) {
			_log.error("Exception:" + e);
		} finally {
			try {
				if (ip1 != null)
					ip1.close();
			} catch (IOException e) {
				_log.error("Exception:" + e);
			}
		}

		Set<Object> keys1 = propFile1.keySet();
		for (Object k : keys1) {
			String key = (String) k;
			String value = propFile1.getProperty(key);
			cacheMap.put(key, value);
		}
		return cacheMap;
	}
}
