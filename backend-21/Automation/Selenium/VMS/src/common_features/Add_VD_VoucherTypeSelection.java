package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Add_VD_VoucherTypeSelection {

	public static boolean promotypeselection (String type) throws Exception{

		try{
			
			FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		 	// Create Properties object    to read Webelement values
		 	Properties prop1 = new Properties();  
		 	//load properties file    to read Webelement values
		 	prop1.load(fileInput1);
				
		 	
		//Thread.sleep(2000);
		WebElement promotype = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("selectvouchertype")));
		Assert.assertTrue(promotype.isDisplayed(), "Voucher type field does not exists");
		Select vouchertype1 = new Select(Launchdriver.driver.findElement(By.xpath(prop1.getProperty("selectvouchertype"))));
		vouchertype1.selectByVisibleText(type);
		System.out.println("		You have selected the voucher type as : " + type );
				
		}catch(AssertionError ae) {
			System.out.println("Assertion Error: No such voucher type exists");
			return false;	
		}catch(Exception e) {
			System.out.println("\n");
			System.out.println("Exception: Invalid Voucher Type");
			return false;	
		}
			return true;
		
		}
}
