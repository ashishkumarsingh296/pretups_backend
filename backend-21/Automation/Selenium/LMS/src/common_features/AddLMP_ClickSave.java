package common_features;

import org.openqa.selenium.By;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class AddLMP_ClickSave {

	
	public static boolean click(String Scenario, String errorfolder ) throws Exception{

		try{
	//Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[contains(@onclick,'optfinalcontribution()')]")).isDisplayed(), "no such button");
	Launchdriver.driver.findElement(By.xpath("//*[contains(@onclick,'optfinalcontribution()')]")).click();
	System.out.println("SAVE button is clicked successfully");
	
	System.out.println("Clicking on CONFIRM");
	Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='confirm']")).isDisplayed());
	Launchdriver.driver.findElement(By.xpath("//*[@name='confirm']")).click();
		}catch(Exception e) {
			common_util_script.GetText.failuretext(Scenario,errorfolder);
			return false;	
		} catch(AssertionError ae) {
			common_util_script.GetText.failuretext(Scenario,errorfolder);
			return false;	
		} 
		return true;
		}
	
	
}
