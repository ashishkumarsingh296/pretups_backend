package common.util;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
/**
 * Load Drivers
 */
public class LaunchDriver {
	
	private static Log _log = LogFactory.getFactory().getInstance(
			LaunchDriver.class.getName());
	public static WebDriver driver = null;

	/**
	 * <h1>Selects Browser and sets its drivers</h1>
	 * 
	 * @return
	 */
	public static WebDriver browser(String browser) {
		HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();
		try {
			if (browser.equals(cacheMap.get("browser"))) {
				System.setProperty("webdriver.chrome.driver",
						cacheMap.get("browserPath"));
				driver = new ChromeDriver();
			}
		} catch (Exception e) {
			_log.error("Exception:" + e);
		}
		return driver;
	}

}
