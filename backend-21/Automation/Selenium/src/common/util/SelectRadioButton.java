package common.util;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Radio Button Selection Logic
 */
public class SelectRadioButton {

	private static Log _log = LogFactory.getFactory().getInstance(
			SelectRadioButton.class.getName());

	/**
	 * <h1>Selects RadioButton based on Value</h1>
	 * 
	 * @return
	 */
	public static boolean selectButtonByValue(String value) {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Selecting the checkbox corresponding to the value");
			}
			String xpath1 = "//*[@value='";
			String xpath2 = "']";
			Assert.assertTrue(
					LaunchDriver.driver.findElement(
							By.xpath(xpath1 + value + xpath2)).isDisplayed(),
					"No such transaction ID " + value + " exists");
			LaunchDriver.driver.findElement(By.xpath(xpath1 + value + xpath2))
					.click();
		} catch (AssertionError ae) {
			_log.error("Exception:" + ae);
			return false;
		} catch (Exception e) {
			_log.error("Exception:" + e);
			return false;
		}
		return true;
	}

	/**
	 * <h1>Selects RadioButton based on Name</h1>
	 * 
	 * @return
	 */
	public static boolean selectButtonByName(String xpathName) {

		try {
			HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();
			WebElement buttonName = LaunchDriver.driver.findElement(By
					.xpath(cacheMap.get(xpathName)));
			buttonName.click();
		} catch (AssertionError ae) {
			_log.error("Exception:" + ae);
			return false;
		} catch (Exception e) {
			_log.error("Exception:" + e);
			return false;
		}
		return true;
	}

}
