package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Modify_VD_Invalid_Click_Submit {

	
	public static boolean click() throws Exception{

		try{
		// Create FileInputStream Object  to read Webelement values
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		// Create Properties object    to read Webelement values
		Properties prop1 = new Properties();  
		//load properties file    to read Webelement values
		prop1.load(fileInput1);		
			
		
		Thread.sleep(1000);		
		Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("mrpsubmit"))).isDisplayed());
		Launchdriver.driver.findElement(By.xpath(prop1.getProperty("mrpsubmit"))).click();
		System.out.println("SUBMIT button is clicked successfully");
	
		
	Assert.assertFalse(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("submitbutton"))).isDisplayed());
	
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
				
			System.out.println("Now clicking on CONFIRM button to add the denomination");
			Launchdriver.driver.findElement(By.xpath(prop1.getProperty("submitbutton"))).click();
			return false;	
		} 
		return true;
		}
	
	
}
