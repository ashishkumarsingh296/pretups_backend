package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class ProfileandVersion {

	public static boolean selectprofileversion (String profile, String version){

		try{
			WebElement loyalityprofile = Launchdriver.driver.findElement(By.xpath("//*[@name='profileSetID']"));
			Assert.assertTrue(loyalityprofile.isDisplayed(), "Profile field does not exists");
			Select loyalityprofile1 = new Select(Launchdriver.driver.findElement(By.xpath("//*[@name='profileSetID']")));
			loyalityprofile1.selectByVisibleText(profile);
			System.out.println("You have selected the promotion type as : " + profile );
			
			System.out.println("Now selecting the version:  " + version );
			WebElement loyalityversion = Launchdriver.driver.findElement(By.xpath("//*[@name='proifleVersionID']"));
			Assert.assertTrue(loyalityversion.isDisplayed(), "Profile field does not exists");
			Select loyalityversion1 = new Select(Launchdriver.driver.findElement(By.xpath("//*[@name='proifleVersionID']")));
			loyalityversion1.selectByVisibleText(version);
			System.out.println("You have selected the version : " + version );

		}catch(Exception e){
			return false;
		}
		
		catch(AssertionError ae) {
			
			return false;
		} 
		return true;
		}
	
}
