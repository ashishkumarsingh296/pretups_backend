package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Add_VD_Click_Submit {

	
	public static boolean click() throws Exception{

		try{
		// Create FileInputStream Object  to read Webelement values
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		// Create Properties object    to read Webelement values
		Properties prop1 = new Properties();  
		//load properties file    to read Webelement values
		prop1.load(fileInput1);		
			
		
		Thread.sleep(1000);		
		Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvd_submit"))).isDisplayed());
		Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvd_submit"))).click();
		System.out.println("SUBMIT button is clicked successfully");
	
		
	Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvd_confirm"))).isDisplayed());
	
	System.out.println("Input values entered by you is Correct. ");
	System.out.println("Now clicking on CONFIRM button to add the denomination");
	Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addvd_confirm"))).click();
	
		}catch(Exception e) {
			System.out.println("Exception : Input values enetered by you is not correct");
			return false;	
		} catch(AssertionError ae) {
			System.out.println("AssetionError : Input values enetered by you is not correct");
			return false;	
		} 
		return true;
		}
	
	
}
