package common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

/**
 * Link Click Logic
 */
public class ClickLink {

	private static Log _log = LogFactory.getFactory().getInstance(
			ClickLink.class.getName());

	/**
	 * <h1>Clicks links based on link text</h1>
	 * 
	 * @return
	 */
	public static boolean byText(String feature) {

		try {
			LaunchDriver.driver.findElement(By.linkText(feature)).click();
			if (_log.isDebugEnabled()) {
				_log.debug(feature + " " + "clicked");
			}

		} catch (AssertionError ae) {
			_log.error("Exception:" + ae);
			return false;
		}
		return true;
	}

}
