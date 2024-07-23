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

public class Add_Active_Profiles {

	public static boolean commoninputvalues (String dt, String denominationname, String profilename ) throws Exception {
		
		try {
			
			
			// Create FileInputStream Object  to read Webelement values
		 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		 	// Create Properties object    to read Webelement values
		 	Properties prop1 = new Properties();  
		 	//load properties file    to read Webelement values
		 	prop1.load(fileInput1);
			
			
			WebElement applicablef = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("applicable_from")));
			Assert.assertTrue(applicablef.isDisplayed(), "Applicable from input parameter does not exists");
			applicablef.sendKeys(dt);	
			System.out.println ("		You have entered the Applicable from: "+ dt );
			
			
			Assert.assertTrue(common_util_script.Selectusingsiblings.selecbutton(denominationname, profilename));
			System.out.println("Profile name is selected");
			
			Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addap_submit"))).click();
			System.out.println("SUBMIT button is clicked successfully");
			
			Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addap_confirm"))).isDisplayed());
			
			System.out.println("Input values entered by you is Correct. ");
			System.out.println("Now clicking on CONFIRM button to add the denomination");
			Launchdriver.driver.findElement(By.xpath(prop1.getProperty("addap_confirm"))).click();
			
						
		}
		catch(AssertionError ae) {
			//common_util_script.Switchwindow.windowhandleclose();		
			System.out.println("No such valid product or module or service in input values");
			return false;	
		}catch(Exception e) {
			System.out.println("No such valid product or module or service in input values1");
			return false;
		}
			return true;
		
		}
	
	}
