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

public class Add_VD_Input_Values {

	public static boolean commoninputvalues (String denominationname, String shortname, String mrp, String payableamnt, String description) throws Exception {
		
		try {
			
			
			// Create FileInputStream Object  to read Webelement values
		 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		 	// Create Properties object    to read Webelement values
		 	Properties prop1 = new Properties();  
		 	//load properties file    to read Webelement values
		 	prop1.load(fileInput1);
			
			
			WebElement dname = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("denomination_name")));
			Assert.assertTrue(dname.isDisplayed(), "DenominationName input parameter does not exists");
			dname.clear();
			dname.sendKeys(denominationname);	
			System.out.println ("		You have entered the DENOMINATION NAME: "+ denominationname );
			
			
			WebElement sname = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("short_name")));
			Assert.assertTrue(dname.isDisplayed(), "Short Name input parameter does not exists");
			sname.clear();
			sname.sendKeys(shortname);	
			System.out.println ("		You have entered the SHORT NAME: "+ shortname );
			
			WebElement price = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("denomination_mrp")));
			Assert.assertTrue(dname.isDisplayed(), "MRP input parameter does not exists");
			price.clear();
			price.sendKeys(mrp);	
			System.out.println ("		You have entered the MRP: "+ mrp );
			
			WebElement payamnt = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("payamount")));
			Assert.assertTrue(dname.isDisplayed(), "PayableAmount input parameter does not exists");
			payamnt.clear();
			payamnt.sendKeys(payableamnt);	
			System.out.println ("		You have entered the Payable Amount: "+ payableamnt );
			
			WebElement desc = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("voucherdescription")));
			Assert.assertTrue(dname.isDisplayed(), "DESCRIPTION input parameter does not exists");
			desc.clear();
			desc.sendKeys(description);	
			System.out.println ("		You have entered the Description: "+ description );
					
		}
		catch(AssertionError ae) {
			common_util_script.Switchwindow.windowhandleclose();		
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
		
		
		
		WebElement servicetype1 = Launchdriver.driver.findElement(By.xpath("//td/table/tbody/tr[3]/td[2]/select"));
		System.out.println("Tagname is :" + servicetype1.getTagName());
		
		if(servicetype1.getTagName().contains("select"))
		  {
			System.out.println("Service type is in dropdown. Now selecting the service type");
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
		  else
		  {
		
				System.out.println("Service type is not enabled. Now selecting the sub-service type");
				
					WebElement sstype = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("subservice_type")));
					Assert.assertTrue(sstype.isDisplayed(), "Sub Service Type input parameter does not exists");
					sstype.sendKeys(subservicetype);	
					System.out.println ("		You have entered the Sub-Service Type: "+ subservicetype );
							
		  }		
	
		}
		catch(AssertionError ae) {
			common_util_script.Switchwindow.windowhandleclose();		
			System.out.println("No such valid product or module or service in input values");
			return false;	
		}catch(Exception e) {
			System.out.println("No such valid product or module or service in input values1");
			return false;
		}
			return true;
		
		}	
		
}
