package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;



public class C2STransfer extends ExtentReportMultipleClasses{

	
	private static String errorfolder = "C2S\\Failure\\";
	
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsChanneladmin();

	}
	@Test(dataProvider = "DP")
	public static void main(String loginid,String password,String amount,String	rechargedno,String servicetype,String subservicetype,String	pin) throws Exception {
		// TODO Auto-generated method stub
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy") ;
		
		FileInputStream fileInput = new FileInputStream(new File(
				"dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);

		test = extent.createTest("To verify that channel user "+loginid+" is able to perform C2S transaction on mobile number "+rechargedno+" of amount "+amount+"");
		
		System.out.println("here");

		try {
			Launchdriver.driver.get(prop.getProperty("url"));
			Launchdriver.driver.findElement(By.name("loginID")).sendKeys(loginid);
			Launchdriver.driver.findElement(By.name("password")).sendKeys(password);
			Launchdriver.driver.findElement(By.name("submit1")).click();
		} catch (Exception e) {
			System.out.println("");
		}
		
		
		test = extent.createTest("To verify that channel user"+loginid+" is able to C2S "+rechargedno);
		
		System.out.println("Channel user is logged in successfully");
		
		//clicking on LMS
		System.out.println("Now clicking on C2S transfer");
		common_features.LMSOptions.clicklms("C2S transfer");
		
		System.out.println("Now clicking on C2S recharge");
		common_features.LMSOptions.clicklms("C2S recharge");
				
		System.out.println("Selecting the service name : ");
		common_util_script.Selectfromdropdown.select("serviceType",servicetype);
		
		System.out.println("Selecting the  subServiceType : ");
		common_util_script.Selectfromdropdown.select("subServiceType",subservicetype );
		
		System.out.println("Entering the subscriber MSISDN : ");
		common_util_script.Sendkeys.sendyourvalue("subscriberMsisdn", rechargedno);
		
		System.out.println("Enter the amount");
		common_util_script.Sendkeys.sendyourvalue("amount", ""+amount);
		
		
	/*	System.out.println("Selecting the  subServiceType : ");
		common_util_script.Selectfromdropdown.select("subServiceType",subservicetype );*/
		
		
	
		
		
		System.out.println("Selecting the  languageCode : ");
		common_util_script.Selectfromdropdown.select("languageCode", "Not applicable");
		
		System.out.println("Enter the pin");
		common_util_script.Sendkeys.sendyourvalue("pin", ""+pin);
		
		System.out.println("Clicking on SUBMIT button");
		common_util_script.ClickButton.click("btnSubmit");
	
		System.out.println("Clicking on CONFIRM button");
		common_util_script.ClickButton.click("btnSubmit");
		
		Thread.sleep(1000);
		//Assert.assertTrue(common_util_script.ClickButton.click("Click here for final notification message."));
		Assert.assertEquals("Click here for final notification message.", Launchdriver.driver.findElement(By.xpath("//tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[3]/td/a")).getText());
		Login.logout();
		

	}
	
	/*@AfterClass
	public void teardown() throws Exception {
		Login.logoutAndcloseDriver();

	}*/
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
 		
 		//read the excel file for invalid credentials
 		return Read_file.excelRead("demo_data.xlsx","C2STransfer");
 	}	
}
