package testcases;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;

public class TC_Logout {

	
	@Test
	public static void logout() throws Exception{
	
			System.out.println("Clicking on Logout button");
			//Launchdriver.driver.switchTo().defaultContent();
			//Launchdriver.driver.switchTo().frame(0);
			try{
			Launchdriver.driver.findElement(By.linkText("Logout")).click();		
			Launchdriver.driver.quit();
			} catch (Exception e1) {
				Launchdriver.driver.switchTo().frame(0);
				Launchdriver.driver.findElement(By.linkText("Logout")).click();	
				Launchdriver.driver.quit();
			}
		}		
	
				
}
