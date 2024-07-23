package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class AddTargetBased {

	public static boolean addtargetprofile(String tdate, String prntamnt, String refbased, String reffdate , String refttdate, String msgconfig, String optinout){
		try {
			WebElement todate = Launchdriver.driver.findElement(By.xpath("//*[@name='applicableToDate']"));
			Assert.assertTrue(todate.isDisplayed(), "todate field does not exists");
			todate.sendKeys(tdate);	
				
			WebElement parentcontri = Launchdriver.driver.findElement(By.xpath("//*[@name='prtContribution']"));
			Assert.assertTrue(parentcontri.isDisplayed(), "todate field does not exists");
			parentcontri.sendKeys(prntamnt);
			
switch (optinout) {
			
			case "Yes":
				WebElement enableoptinout = Launchdriver.driver.findElement(By.xpath("//*[@name='optInOutTarget']"));
				Assert.assertTrue(enableoptinout.isDisplayed(), "optinout field does not exists");
				enableoptinout.click();
				break;
			case "No" :
				break;
				}		
				
			
			switch (refbased) {
			
			case "Yes":
				addrefdate(reffdate, refttdate);
				break;
			case "No" :
				break;
				}		
					
			switch (msgconfig) {
			
			case "Yes":
				System.out.println("Checking if msgconfig is present");
				WebElement enablemessageconfig = Launchdriver.driver.findElement(By.xpath("//*[@name='msgConfEnableFlag']"));   //For BL
				//WebElement enablemessageconfig = Launchdriver.driver.findElement(By.xpath("//*[@id='volume']/td/table/tbody/tr[6]/td[3]/input"));
				
				enablemessageconfig.click();
				System.out.println("msgconfig is clicked successfully");
				break;
			case "No" :
				break;
				}			
			
					
			System.out.println("Input values are provided. Now assigning volume profile.");
			
			WebElement avp = Launchdriver.driver.findElement(By.xpath("//*[contains(@onclick,'showVolumeProfileDetails')]")); 
			
			Assert.assertTrue(avp.isDisplayed());
			avp.click();
			
		}
		catch(AssertionError ae) {
			System.out.println("No such valid username/group in target based profile");
			return false;	
		}
			return true;
		
		}
	
	private static void addrefdate(String reffdate, String refttdate) {
		
		WebElement refbased = Launchdriver.driver.findElement(By.xpath("//*[@name='referenceBasedFlag']"));
		Assert.assertTrue(refbased.isDisplayed(), "optinout field does not exists");
		refbased.click();
		
		WebElement reffromdate = Launchdriver.driver.findElement(By.xpath("//*[@name='refApplicableFromDate']"));
		Assert.assertTrue(reffromdate.isDisplayed(), "ref todate field does not exists");
		reffromdate.sendKeys(reffdate);
		
		WebElement reftodate = Launchdriver.driver.findElement(By.xpath("//*[@name='refApplicableToDate']"));
		Assert.assertTrue(reftodate.isDisplayed(), "ref todate field does not exists");
		reftodate.sendKeys(refttdate);
	// TODO Auto-generated method stub
	
}
	
}
