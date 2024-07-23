package common_util_script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class ClickButton {
	
	public static boolean click(String buttonname ) throws Exception{

	try{

Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='" + buttonname + "']")).isDisplayed());
Launchdriver.driver.findElement(By.xpath("//*[@name='" + buttonname + "']")).click();
	}catch(Exception e) {
		System.out.println("Your button does not exists or your xpath is wrong or wrong input value entered");
		String errormessage = Launchdriver.driver.findElement(By.xpath("//td/table/tbody/tr[2]/td[2]/ol/li")).getText();
		System.out.println("Error message is: " + errormessage);
				return false;	
	} catch(AssertionError ae) {
		System.out.println("Your button does not exists or your xpath is wrong or wrong input value entered");
		String errormessage = Launchdriver.driver.findElement(By.xpath("//td/table/tbody/tr[2]/td[2]/ol/li")).getText();
		System.out.println("Error message is: " + errormessage);
		return false;	
	} 
	return true;
	}
	
	
}
