package common.util;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Set Wait Time
 */
public class WaitTime {

	private static Log _log = LogFactory.getFactory().getInstance(
			WaitTime.class.getName());

	/**
	 * <h1>Sets wait Time during execution.Takes time in seconds as parameter</h1>
	 * 
	 * @return
	 */
	public static boolean timeToWait(long time) {
		try {
			LaunchDriver.driver.manage().timeouts()
					.implicitlyWait(time, TimeUnit.SECONDS);

		} catch (Exception e) {
			_log.error("Exception:" + e);
			return false;
		}
		return true;
	}
}
