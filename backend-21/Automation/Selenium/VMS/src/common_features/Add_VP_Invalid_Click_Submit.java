package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Add_VP_Invalid_Click_Submit {

	
	public static boolean click() throws Exception{

		try{
		// Create FileInputStream Object  to read Webelement values
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		// Create Properties object    to read Webelement values
		Properties prop1 = new Properties();  
		//load properties file    to read Webelement values
		prop1.load(fileInput1);		
	Thread.sleep(1000);		
	Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvp_submit"))).isDisplayed());
	Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvp_submit"))).click();
	System.out.println("SUBMIT button is clicked successfully");
	
	Assert.assertFalse(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvp_confirm"))).isDisplayed());
	
		}catch(Exception e) {
			System.out.println("Exception : Input values enetered by you is not correct");
			return false;	
		} catch(AssertionError ae) {
			// Create FileInputStream Object  to read Webelement values
			FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
			// Create Properties object    to read Webelement values
			Properties prop1 = new Properties();  
			//load properties file    to read Webelement values
			prop1.load(fileInput1);		
		
			System.out.println("Input values entered by you is Correct. ");
			System.out.println("Now clicking on CONFIRM button to add the profile");
			Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvp_confirm"))).click();
			
			return false;	
		} 
		return true;
		}
	
	
}
