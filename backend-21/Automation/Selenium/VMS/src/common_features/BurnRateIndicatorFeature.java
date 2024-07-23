package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class BurnRateIndicatorFeature {

	public static boolean clicklink (String feature){

		try{
			Launchdriver.driver.findElement(By.linkText(feature)).click();

		}catch(AssertionError ae) {
			System.out.println("Your feature link is not available");
			return false;
		} 
		return true;
	}
	
	public static boolean clickSubmit () throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();
		prop1.load(fileInput1);
		try{
			Launchdriver.driver.findElement(By.xpath(prop1.getProperty("burnRateSubmit"))).click();
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}
	

	public static boolean enterMsisdn (String msisdn) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();
		prop1.load(fileInput1);

		try{
			WebElement msisdnTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("msisdn")));
			Assert.assertTrue(msisdnTextBox.isDisplayed(), "MSISDN text box does not exists");
			msisdnTextBox.clear();
			msisdnTextBox.sendKeys(msisdn);
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}

	public static boolean assertMSISDN(String msisdn) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();
		prop1.load(fileInput1);

		try{
			WebElement msisdnTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("msisdnNext")));
			Assert.assertEquals(msisdnTextBox.getText(), msisdn);
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}
	
	public static boolean assertVoucherProfile(String profile) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();
		prop1.load(fileInput1);

		try{
			WebElement voucherProfile = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("voucherProfile")));
			Assert.assertEquals(voucherProfile.getText(), profile);
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}
	
	public static boolean assertVoucherProfileMSISDN(String msisdn, String profile) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();
		prop1.load(fileInput1);

		try{
			WebElement msisdnTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("msisdnNext")));
			Assert.assertEquals(msisdnTextBox.getText(), msisdn);
			WebElement voucherProfile = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("voucherProfile")));
			Assert.assertEquals(voucherProfile.getText(), profile);
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}
	
	
	public static boolean selectVoucherDetails (String denomination, String profile) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();  
		prop1.load(fileInput1);

		try{
			WebElement denominationElement = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("denomination")));
			Assert.assertTrue(denominationElement.isDisplayed(), "Voucher Denomination Dropdown does not exists");
			Select denominationSelection = new Select(denominationElement);
			denominationSelection.selectByVisibleText(denomination);
			
			WebElement profileElement = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("profile")));
			Assert.assertTrue(profileElement.isDisplayed(), "Voucher Profile dropdown does not exists");
			Select profileSelection = new Select(profileElement);
			profileSelection.selectByVisibleText(profile);
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}
	
	public static boolean enterCombinedDetails (String msisdn, String denomination, String profile) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();  
		prop1.load(fileInput1);

		try{
			WebElement msisdnTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("optionalMSISDN")));
			Assert.assertTrue(msisdnTextBox.isDisplayed(), "Msisdn text box does not exists");
			msisdnTextBox.clear();
			msisdnTextBox.sendKeys(msisdn);
			
			WebElement denominationElement = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("optionalDenomination")));
			Assert.assertTrue(denominationElement.isDisplayed(), "Voucher Denomination dropdown does not exists");
			Select denominationSelection = new Select(denominationElement);
			denominationSelection.selectByVisibleText(denomination);
			
			WebElement profileElement = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("optionalProfile")));
			Assert.assertTrue(profileElement.isDisplayed(), "Voucher Profile dropdown does not exists");
			Select profileSelection = new Select(profileElement);
			profileSelection.selectByVisibleText(profile);
			
			
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}
	
	public static boolean enterDates (String distributedFromDate, String distributedToDate, String rechargedFromDate, String rechargedToDate) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
		Properties prop1 = new Properties();  
		prop1.load(fileInput1);

		try{
			WebElement distributedFromDateTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("distributedFromDate")));
			Assert.assertTrue(distributedFromDateTextBox.isDisplayed(), "Distributed From Date text box does not exists");
			distributedFromDateTextBox.clear();
			distributedFromDateTextBox.sendKeys(distributedFromDate);
			
			WebElement distributedToDateTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("distributedToDate")));
			Assert.assertTrue(distributedToDateTextBox.isDisplayed(), "Distributed To Date text box does not exists");
			distributedToDateTextBox.clear();
			distributedToDateTextBox.sendKeys(distributedToDate);
			
			WebElement recahrgedFromDateTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("rechargedFromDate")));
			Assert.assertTrue(recahrgedFromDateTextBox.isDisplayed(), "Recharged From Date text box does not exists");
			recahrgedFromDateTextBox.clear();
			recahrgedFromDateTextBox.sendKeys(rechargedFromDate);
			
			WebElement recahrgedToDateTextBox = Launchdriver.driver.findElement(By.xpath(prop1.getProperty("rechargedToDate")));
			Assert.assertTrue(recahrgedToDateTextBox.isDisplayed(), "Recharged To Date text box does not exists");
			recahrgedToDateTextBox.clear();
			recahrgedToDateTextBox.sendKeys(rechargedToDate);
		}catch(AssertionError ae) {
			return false;
		} 
		return true;
	}
}
