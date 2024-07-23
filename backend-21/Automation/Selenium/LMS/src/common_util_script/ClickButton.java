package common_util_script;

import org.openqa.selenium.By;
import org.testng.Assert;

public class ClickButton {
	
	public static boolean click(String buttonname ) throws Exception{

	try{

Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='" + buttonname + "']")).isDisplayed());
Launchdriver.driver.findElement(By.xpath("//*[@name='" + buttonname + "']")).click();
	}catch(Exception e) {
		System.out.println("You button does not exists or you xpath is wrong");
		return false;	
	} catch(AssertionError ae) {
		System.out.println("You button does not exists or your xpath is wrong");
		return false;	
	} 
	return true;
	}
	
	
}
