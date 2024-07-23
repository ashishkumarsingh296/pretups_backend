package common_util_script;

import org.openqa.selenium.By;
import org.testng.Assert;

public class Sendkeys {

	public static boolean sendyourvalue (String buttonname, String yourvalue ) throws Exception{

		try{
	Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='" + buttonname + "']")).isDisplayed());
	Launchdriver.driver.findElement(By.xpath("//*[@name='" + buttonname + "']")).sendKeys(yourvalue);
		}catch(Exception e) {
			return false;	
		} catch(AssertionError ae) {
			return false;	
		} 
		return true;
		}
	
}
