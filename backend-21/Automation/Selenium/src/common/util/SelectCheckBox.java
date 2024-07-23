package common.util;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * CheckBox Selection Logic
 */
public class SelectCheckBox {

	/**
	 * <h1>Selects CheckBox by Name</h1>
	 * 
	 * @return
	 */
	public static void selectByValue(String xpathName, String value) {
		String xpath1 = "//*[@value='";
		String xpath2 = "']";
		String splitBy = ",";
		HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();
		// WebElement checkBox =
		// Launchdriver.driver.findElement(By.xpath(cacheMap.get(xpathName)));

		String splitValue[] = value.split(splitBy);
		for (int i = 0; i < splitValue.length; i++) {
			WebElement ele = LaunchDriver.driver.findElement(By.xpath(xpath1
					+ splitValue[i] + xpath2));
			if (!ele.isSelected())
				ele.click();
		}
	}
}
