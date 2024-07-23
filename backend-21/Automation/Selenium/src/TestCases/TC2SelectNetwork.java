package testcases;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import common.util.LaunchDriver;
import common.util.LoadPropertiesFile;
/**
 * Select Network after logging-Inn
 */
public class TC2SelectNetwork {

	private static Log _log = LogFactory.getFactory().getInstance(
			TC2SelectNetwork.class.getName());

	/**
	 * <h1>Tests the network selection logic</h1>
	 * 
	 * @return
	 */
	@Test
	public static void select_valid_network() {

		if (_log.isDebugEnabled()) {
			_log.debug("Scenario : Select Network after logging-Inn");
		}
		HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();

		common.util.SelectRadioButton.selectButtonByValue(cacheMap
				.get("network"));
		common.util.ClickButton.clickByName("submit1");

		if (_log.isDebugEnabled()) {
			_log.debug("Selected Network and clicked Submit button");
		}

		LaunchDriver.driver.switchTo().frame(0);
		WebElement logout = LaunchDriver.driver.findElement(By
				.cssSelector(cacheMap.get("logout")));
		Assert.assertTrue(logout.isDisplayed(), "Login is not successfull");

	}
}
