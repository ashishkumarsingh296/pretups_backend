package common.util;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Browser window switch
 */
public class SwitchWindow {

	private static Log _log = LogFactory.getFactory().getInstance(
			SwitchWindow.class.getName());
	private static String homepage = LaunchDriver.driver.getWindowHandle();

	/**
	 * <h1>Switches between different browser windows</h1>
	 * 
	 * @return
	 */
	public static boolean windowHandle() {
		try {
			homepage = LaunchDriver.driver.getWindowHandle();
			Set<String> windows = LaunchDriver.driver.getWindowHandles();
			Iterator iterator = windows.iterator();
			String currentWindowID;
			while (iterator.hasNext()) {
				currentWindowID = iterator.next().toString();
				if (!currentWindowID.equals(homepage))
					;
				LaunchDriver.driver.switchTo().window(currentWindowID);
			}
		} catch (Exception e) {
			_log.error("Exception:" + e);
			return false;
		}
		return true;
	}

	/**
	 * <h1>Switches back to main browser windows</h1>
	 * 
	 * @return
	 */
	public static boolean windowHandleClose() {
		try {
			// bringing the control back to the main window

			LaunchDriver.driver.switchTo().window(homepage);
			// Launchdriver.driver.close();

		} catch (Exception e) {
			_log.error("Exception:" + e);
			return false;
		}
		return true;
	}
}
