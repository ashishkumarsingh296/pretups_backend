package common_util_script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class Selectfromdropdown {
	
	public static boolean select (String xpathname, String value ) throws Exception{

		try{
			System.out.println("Your xpath name is : " + xpathname );	
			WebElement lms = Launchdriver.driver.findElement(By.name(xpathname));
			Assert.assertTrue(lms.isDisplayed(), "Lms option does not exists");
			Select LMS1 = new Select(Launchdriver.driver.findElement(By.name(xpathname)));
			LMS1.selectByVisibleText(value);
			System.out.println("You have selected dropdown value as : " + value );	

	
		}catch(Exception e) {
			System.out.println("No such element exception. Your dropdown field does not exists");
			return false;	
		} catch(AssertionError ae) {
			System.out.println("Assertion exception. Your dropdown field does not exists");
			return false;	
		} 
		return true;
		}

}
