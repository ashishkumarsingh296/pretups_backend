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

public class Add_VP_Input_Values {

	public static boolean commoninputvalues (String mrp, String profilename, String shortname, String Minq, String Perfq, String tt, String validity, String expiryp) throws Exception {
		
		try {
			
			
			// Create FileInputStream Object  to read Webelement values
		 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		 	// Create Properties object    to read Webelement values
		 	Properties prop1 = new Properties();  
		 	//load properties file    to read Webelement values
		 	prop1.load(fileInput1);
			
		 	Thread.sleep(2000);
		 	WebElement mrpp = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("mrprice")));

			System.out.println ("		You are selecting the MRP as: "+ mrp );
			Assert.assertTrue(mrpp.isDisplayed(), "Profile Name input parameter does not exists");
			Select mrpp1 = new Select(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("mrprice"))));
			mrpp1.selectByVisibleText(mrp);
			System.out.println ("		You have selected the MRP as: "+ mrp );
			
		 	
			WebElement pname = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("profile_name")));
			Assert.assertTrue(pname.isDisplayed(), "Profile Name input parameter does not exists");
			pname.sendKeys(profilename);	
			System.out.println ("		You have entered the Profile NAME: "+ profilename );
			
			
			WebElement shortn = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("shortname")));
			Assert.assertTrue(shortn.isDisplayed(), "Short Name input parameter does not exists");
			shortn.sendKeys(shortname);	
			System.out.println ("		You have entered the SHORT NAME: "+ shortname );
			
			WebElement minqq = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("minimumquantity")));
			Assert.assertTrue(minqq.isDisplayed(), "Minimum Quantity parameter does not exists");
			minqq.sendKeys(Minq);	
			System.out.println ("		You have entered the Minimum Quanity: "+ Minq );
			
			WebElement perfoq = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("performancequantity")));
			Assert.assertTrue(perfoq.isDisplayed(), "Performance Quantity input parameter does not exists");
			perfoq.sendKeys(Perfq);	
			System.out.println ("		You have entered the Performance Quantity: "+ Perfq );
			
			WebElement talkt = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("talktime")));
			Assert.assertTrue(talkt.isDisplayed(), "Talk Time input parameter does not exists");
			talkt.sendKeys(tt);	
			System.out.println ("		You have entered the Talk Time: "+ tt );
				
			WebElement valid = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("validitty")));
			Assert.assertTrue(valid.isDisplayed(), "DESCRIPTION input parameter does not exists");
			valid.sendKeys(validity);	
			System.out.println ("		You have entered the Validity: "+ validity );
			
			WebElement expiryppp = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("expiryperiod")));
			Assert.assertTrue(expiryppp.isDisplayed(), "DESCRIPTION input parameter does not exists");
			expiryppp.sendKeys(expiryp);	
			System.out.println ("		You have entered the Expiry Period: "+ expiryp );
			
			
			
			
					
		}
		catch(AssertionError ae) {	
			System.out.println("No such valid product or module or service in input values");
			return false;	
		}catch(Exception e) {
			System.out.println("No such valid product or module or service in input values1");
			return false;
		}
			return true;
		
		}
	
	
	
public static boolean physicalvdinputvalue (String servicetype, String subservicetype) throws Exception {
		
		try {
		
		// Create FileInputStream Object  to read Webelement values
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		// Create Properties object    to read Webelement values
		Properties prop1 = new Properties();  
		//load properties file    to read Webelement values
		prop1.load(fileInput1);	
				
		System.out.println("Now selecting the service type");
		WebElement service_type = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("servicetype_selection")));
		Assert.assertTrue(service_type.isDisplayed(), "Service Type Field does not exists");
		Select service_type1 = new Select(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("servicetype_selection"))));
		service_type1.selectByVisibleText(servicetype);
		System.out.println("		You have selected the ServiceType : " + servicetype );

		System.out.println("Now selecting the sub-service type");
		
		switch (servicetype) {
		
		case "EVD":				
			break;
			
		case "RC":	
			WebElement sstype = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("subservice_type")));
			Assert.assertTrue(sstype.isDisplayed(), "Sub Service Type input parameter does not exists");
			sstype.sendKeys(subservicetype);	
			System.out.println ("		You have entered the Sub-Service Type: "+ subservicetype );
			
			break;
			
		default:
			break;
		
		}
	
		}
		catch(AssertionError ae) {
				
			System.out.println("No such valid product or module or service in input values");
			return false;	
		}catch(Exception e) {
			System.out.println("No such valid product or module or service in input values1");
			return false;
		}
			return true;
		
		}
	
	
	
public static boolean servicevdinputvalue (String subservicetype) throws Exception {
	
	try {
	
	// Create FileInputStream Object  to read Webelement values
	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
	// Create Properties object    to read Webelement values
	Properties prop1 = new Properties();  
	//load properties file    to read Webelement values
	prop1.load(fileInput1);	
	
	System.out.println("Now selecting the sub service type");
	WebElement subservicetyp1e = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("subservice_type")));
	Assert.assertTrue(subservicetyp1e.isDisplayed(), "Sub Service Type Field does not exists");
	Select subservicetyp1e1 = new Select(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("subservice_type"))));
	subservicetyp1e1.selectByVisibleText(subservicetype);
	System.out.println("		You have selected the Sub ServiceType : " + subservicetype );
		
	System.out.println("Now selecting the sub-service type");
	}
	catch(AssertionError ae) {
			
		System.out.println("No such valid product or module or service in input values");
		return false;	
	}catch(Exception e) {
		System.out.println("No such valid product or module or service in input values1");
		return false;
	}
		return true;
	
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
}
