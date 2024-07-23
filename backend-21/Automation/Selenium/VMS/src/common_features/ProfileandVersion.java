package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class ProfileandVersion {

	public static boolean selectprofileversion (String profile, String version) throws Exception{
		
		// Create FileInputStream Object  to read Webelement values
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		
		// Create Properties object    to read Webelement values
		Properties prop1 = new Properties();  
		
		//load properties file    to read Webelement values
		prop1.load(fileInput1);

		try{
			WebElement loyalityprofile = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("selectprofile")));
			Assert.assertTrue(loyalityprofile.isDisplayed(), "Profile field does not exists");
			Select loyalityprofile1 = new Select(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("selectprofile"))));
			loyalityprofile1.selectByVisibleText(profile);
			System.out.println("You have selected the promotion type as : " + profile );
			
			System.out.println("Now selecting the version:  " + version );
			WebElement loyalityversion = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("selectversion")));
			Assert.assertTrue(loyalityversion.isDisplayed(), "Profile field does not exists");
			Select loyalityversion1 = new Select(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("selectversion"))));
			loyalityversion1.selectByVisibleText(version);
			System.out.println("You have selected the version : " + version );

		}catch(AssertionError ae) {
			
			return false;
		} 
		return true;
		}
	
}