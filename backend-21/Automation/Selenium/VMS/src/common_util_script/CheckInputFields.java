package common_util_script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class CheckInputFields {
	
	public static boolean click(String inputfieldname, String inputfieldvalue) throws Exception{

	try{

		Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='" + inputfieldname + "']")).isDisplayed());
		Launchdriver.driver.findElement(By.xpath("//*[@name='" + inputfieldname + "']")).sendKeys(inputfieldvalue);
		
	}catch(Exception e) {
		System.out.println("Your button does not exists or your xpath is wrong or wrong input value entered");
		System.out.println("Exception: Input field " + inputfieldname +  " does not exists");
				return false;	
	} catch(AssertionError ae) {
		System.out.println("Your button does not exists or your xpath is wrong or wrong input value entered");
		System.out.println("Assertion: Input field " + inputfieldname +  " does not exists");
		return false;	
	} 
	return true;
	}
	
	
}
