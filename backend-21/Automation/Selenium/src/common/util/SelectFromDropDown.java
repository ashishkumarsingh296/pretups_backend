package common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * DropDown Selection Logic
 */
public class SelectFromDropDown {

	private static Log _log = LogFactory.getFactory().getInstance(
			SelectFromDropDown.class.getName());

	/**
	 * <h1>Selects DropDown Entity by Name</h1>
	 * 
	 * @return
	 */
	public static boolean selectByName(String xpathname, String value) {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Your xpath name is : " + xpathname);
			}
			WebElement dropBox = LaunchDriver.driver.findElement(By
					.name(xpathname));
			Select dropBoxValue = new Select(LaunchDriver.driver.findElement(By
					.name(xpathname)));
			dropBoxValue.selectByVisibleText(value);
			if (_log.isDebugEnabled()) {
				_log.debug("You have selected dropdown value as : " + value);
			}

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
