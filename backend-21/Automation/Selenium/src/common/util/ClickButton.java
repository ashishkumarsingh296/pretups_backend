package common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Button Click Logic
 */
public class ClickButton {

	private static Log _log = LogFactory.getFactory().getInstance(
			ClickButton.class.getName());

	/**
	 * <h1>Clicks button based on button name</h1>
	 * 
	 * @return
	 */
	public static boolean clickByName(String buttonname) {
		final String methodName = "clickByName";
		try {

			Assert.assertTrue(LaunchDriver.driver.findElement(
					By.xpath("//*[@name='" + buttonname + "']")).isDisplayed());
			LaunchDriver.driver.findElement(
					By.xpath("//*[@name='" + buttonname + "']")).click();
		} catch (AssertionError ae) {
			String errormessage = LaunchDriver.driver.findElement(
					By.xpath("//td/table/tbody/tr[2]/td[2]/ol/li")).getText();
			_log.error("Exception:" + errormessage);
			return false;
		} catch (Exception e) {
			String errormessage = LaunchDriver.driver.findElement(
					By.xpath("//td/table/tbody/tr[2]/td[2]/ol/li")).getText();
			_log.error("Exception:" + errormessage);
			return false;
		}
		return true;
	}

}
