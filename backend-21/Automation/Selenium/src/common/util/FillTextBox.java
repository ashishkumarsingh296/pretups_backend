package common.util;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * TextBox data filling logic
 */
public class FillTextBox {

	private static Log _log = LogFactory.getFactory().getInstance(
			FillTextBox.class.getName());

	/**
	 * <h1>Selects textbox based on name</h1>
	 * 
	 * @return
	 */
	public static void addTextByName(String xpathName, String value) {
		HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();
		try {
			WebElement textBox = LaunchDriver.driver.findElement(By
					.xpath(cacheMap.get(xpathName)));

			textBox.sendKeys(value);
		} catch (AssertionError ae) {
			_log.error("Exception:" + ae);

		}

	}

}
