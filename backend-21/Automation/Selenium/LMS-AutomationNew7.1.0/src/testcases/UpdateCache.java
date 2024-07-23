package testcases;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;

public class UpdateCache {

	@BeforeClass
	public static void beforetest ()throws Exception {
		// TODO Auto-generated method stub

		System.out.println("");
		System.out.println("TEST SCENARIO : Updating Cache" );	
		
		//login to the  GUI using the valid credentials
		Login.loginAsNetworkadmin();
	}
	
	@Test
	public static void UpdateCacheScript () throws Exception {
		// TODO Auto-generated method stub
		
		//clicking on LMS
		System.out.println("Now clicking on Masters");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Masters"),"Masters link does not exists");
		System.out.println("Masters is clicked successfully");
		
		System.out.println("Now clicking on Associate profile option");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Update cache"),"Update cache link is not available");
		System.out.println("Update Cache is clicked successfully");
		
		System.out.println("Now clicking on ALL");
		common_util_script.ClickButton.click("cacheAll");
		System.out.println("ALL option is selected");
		
		System.out.println("Now clicking on submitButton");
		common_util_script.ClickButton.click("submitButton");
		
		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//table[4]/tbody/tr[1]/td/table/tbody/tr/td"));
		System.out.println("Actual Success message is : " + actualsuccessmessage.getText() );
				
		String expectedsuccessmessage = "Cache of [1] WEB has been updated successufully"; 
		
		System.out.println("Expected Success message is : " + expectedsuccessmessage );
		
		System.out.println("Now comparing the success message");
		Assert.assertEquals(actualsuccessmessage.getText(), expectedsuccessmessage, "Actual message is not as per the expected message. Test case is failed");
		
		System.out.println("Cache is updated successfully");
	}
	
	@AfterClass
	public void teardown() throws Exception
	{
	  	//quit the driver once the method is completed
		
		
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
