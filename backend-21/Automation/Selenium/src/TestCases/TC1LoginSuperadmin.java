package testcases;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import common.features.Login;
import common.util.LaunchBrowser;
import common.util.LaunchDriver;
import common.util.LoadPropertiesFile;
/**
 * Login to PreTUPS application with Valid credentials
 */
public class TC1LoginSuperadmin {

	private static Log _log = LogFactory.getFactory().getInstance(TC1LoginSuperadmin.class.getName());
	
	/**
	 * <h1>Tests the login logic with Super Admin credentials</h1>
	 * 
	 * @return
	 */
	@Test
 	public static void login_with_valid_credentials ()  {
	
	HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();
	 if (_log.isDebugEnabled()) {
         _log.debug("Scenario : Login to PreTUPS application with Valid credentials using object.properties");
     }
		
	LaunchDriver.driver = LaunchDriver.browser("chrome");

	LaunchBrowser.launch(cacheMap.get("url"));

	Login loginpage = PageFactory.initElements(LaunchDriver.driver, Login.class);
	
	Assert.assertTrue(loginpage.login_page(cacheMap.get("super_user"),cacheMap.get("super_password")));

	LaunchDriver.driver.switchTo().frame(0);
	WebElement logout = LaunchDriver.driver.findElement(By.cssSelector(cacheMap.get("logout")));
	Assert.assertTrue(logout.isDisplayed(), "Login is not successfull");
	}			
}