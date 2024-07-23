package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.Assertion;

import common_util_script.Launchdriver;

public class AddTransactionBasedProfile {

	public static boolean addtransactionprofile (String ftime, String tdate, String ttime, String msgconfig, String optinout){
	try {
			WebElement fromtime = Launchdriver.driver.findElement(By.xpath("//*[@name='applicableFromHour']"));
			Assert.assertTrue(fromtime.isDisplayed(), "fromdate field does not exists");
			fromtime.sendKeys(ftime);	
			
			WebElement todate = Launchdriver.driver.findElement(By.xpath("//*[@name='applicableToDate']"));
			Assert.assertTrue(todate.isDisplayed(), "todate field does not exists");
			todate.sendKeys(tdate);	
			
			WebElement totime = Launchdriver.driver.findElement(By.xpath("//*[@name='applicableToHour']"));
			Assert.assertTrue(totime.isDisplayed(), "totime field does not exists");
			totime.sendKeys(ttime);	
			
			switch (msgconfig) {
			
			case "Yes":
				System.out.println("Enabling the msgconfig service. You have selected the option as : " + msgconfig );
				WebElement enablemessageconfig = Launchdriver.driver.findElement(By.xpath("//*[@name='msgConfEnableFlag']"));   //For BL
				//WebElement enablemessageconfig = Launchdriver.driver.findElement(By.xpath("//*[@id='loyaltypoint']/td/table/tbody/tr[1]/td[4]/input"));
				
				enablemessageconfig.click();
				System.out.println("msgconfig is clicked successfully");
				break;
			case "No" :
				break;
				}		
			
			switch (optinout) {
			
			case "Yes":
				WebElement enableoptinout = Launchdriver.driver.findElement(By.xpath("//*[@name='optInOut']"));
				Assert.assertTrue(enableoptinout.isDisplayed(), "optinout field does not exists");
				enableoptinout.click();
				break;
			case "No" :
				break;
				}	

			System.out.println("Input values are provided");
			WebElement atp = Launchdriver.driver.findElement(By.xpath("//*[contains(@onclick,'showTransactionProfileDetails')]"));
			Assert.assertTrue(atp.isDisplayed());
			atp.click();
			
			
	} catch(Exception ae) {
		System.out.println("No such valid username/group in transaction based profile");
		return false;	
	} catch(AssertionError ae) {
		System.out.println("The element xpath mentioned in the transaction based profile script is not correct");
		return false;	
	}
		return true;
	
	}
}
