package common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Launch Browser
 */
public class LaunchBrowser {
	
	private static Log _log = LogFactory.getFactory().getInstance(
			LaunchBrowser.class.getName());

	/**
	 * <h1>Launches Browser based on URL</h1>
	 * 
	 * @return
	 */
	public static boolean launch(String url) {
		try {
			LaunchDriver.driver.get(url);
			LaunchDriver.driver.manage().window().maximize();
			if (_log.isDebugEnabled()) {
				_log.debug("Browser launched successfully");
			}
		} catch (Exception e) {
			_log.error("Exception:" + e);
			return false;
		}
		return true;
	}

}
