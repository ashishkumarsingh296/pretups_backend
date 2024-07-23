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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.DB_Connection;
import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;



public class TC6_C2S extends ExtentReportMultipleClasses{
	
	@BeforeClass
	public static void beforetest ()throws Exception {
		
		// Create FileInputStream Object  to read the credentials 
	 		 FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));  
	 	// Create Properties object  to read the credentials
	 		 Properties prop = new Properties();  
	 	//load properties file  to read the credentials
	 		 prop.load(fileInput);   
	 	
	 		// Create FileInputStream Object  to read Webelement values
	 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
	 	// Create Properties object    to read Webelement values
	 		 Properties prop1 = new Properties();  
	 	//load properties file    to read Webelement values
	 		 prop1.load(fileInput1);
		 	
	 	System.out.println("Scenario : Login to PreTUPS application with Valid credentials using object.properties");
			
		System.out.println("Enter a valid LoginID and Password");
		
		
		//launching the browser
		Launchdriver.driver = Launchdriver.browser("chrome");
		
		//launching the URL
		common_util_script.Launch_Browser.launch("http://172.16.11.120:7878/pretups/");
	}
	
	
	
	@Test(dataProvider = "DP")
	public static void c2stransfer(String login,String pass, String amount, String mobileno, String servicetype, String sub,String pin ) throws Exception {
		// TODO Auto-generated method stub
		
		test = extent.createTest("To verify that user "+login+" is able to perform C2S Recharge on "+mobileno+" of amount "+amount+"");

		try{
			
		Launchdriver.driver.get("http://172.16.11.120:7878/pretups/");
		WebElement lang=Launchdriver.driver.findElement(By.name("language"));
		Select option=new Select(lang);
		option.selectByVisibleText("English");
		Launchdriver.driver.findElement(By.name("loginID")).sendKeys(login);
		Launchdriver.driver.findElement(By.name("password")).sendKeys(pass);
		
		Launchdriver.driver.findElement(By.name("submit1")).click();;
		Launchdriver.driver.switchTo().frame(0);
		}catch(Exception e){
			System.out.println("");
		}
		
		
		Date date = new Date() ;
		
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy") ;
		
		System.out.println("");
		System.out.println("");
		System.out.println("New Scenario");
		//clicking on LMS
		System.out.println("Now clicking on C2S transfer");
		common_features.LMSOptions.clicklms("C2S transfer");
		
		System.out.println("Now clicking on C2S recharge");
		common_features.LMSOptions.clicklms("C2S recharge");
				
		/*System.out.println("Selecting the service name : ");
		Assert.assertTrue(common_util_script.Selectfromdropdown.select("serviceType", servicetype),"You servicetype does not exists");
		System.out.println("Service name selected is : "+ servicetype);*/
		 
		/*System.out.println("Entering the subscriber MSISDN : ");
		common_util_script.Sendkeys.sendyourvalue("subscriberMsisdn", ""+mobileno);
		System.out.println("MSISDN Entered is : "+ mobileno);*/
		
		
		System.out.println("Selecting the service name : ");
		Assert.assertTrue(common_util_script.Selectfromdropdown.select("subServiceType", sub),"You subServiceType does not exists");
		System.out.println("Service name selected is : "+ sub);
		
		
		System.out.println("Entering the subscriber MSISDN : ");
		common_util_script.Sendkeys.sendyourvalue("subscriberMsisdn", ""+mobileno);
		System.out.println("MSISDN Entered is : "+ mobileno);
		
				
		System.out.println("Enter the amount");
		common_util_script.Sendkeys.sendyourvalue("amount", amount);	
		System.out.println("Amount Entered is : "+ amount);
		
		
		/*System.out.println("Selecting the service name : ");
		Assert.assertTrue(common_util_script.Selectfromdropdown.select("subServiceType", sub),"You subServiceType does not exists");
		System.out.println("Service name selected is : "+ sub);*/
				
		System.out.println("Enter your valid PIN");
		Assert.assertTrue(common_util_script.Sendkeys.sendyourvalue("pin", pin));
		System.out.println("PIN Entered is : "+ pin);
		
		
		
		
		System.out.println("Clicking on SUBMIT button");
		common_util_script.ClickButton.click("btnSubmit");
		
		
		
		System.out.println("Checking if values entered are valid");
		//Assert.assertTrue(common_util_script.CheckButton.click("btnCancel"));
		Assert.assertTrue(common_util_script.ClickButton.click("btnSubmit"));
		Assert.assertTrue(common_util_script.ClickButton.click("Click here for final notification message."));
			
		common_util_script.Get_screenshot.success("c2s", mobileno + "c2s recharge");
		
		
		
	}
	
	@AfterClass
	public void teardown() throws Exception
	{
	  	//quit the driver once the method is completed
		
		
		System.out.println("Clicking on Logout button");
		//Launchdriver.driver.switchTo().defaultContent();
		//Launchdriver.driver.switchTo().frame(0);
		try{
		Launchdriver.driver.findElement(By.linkText("Logout")).click();	
		System.out.println("Logout successfull 1");
		Launchdriver.driver.quit();
		} catch (Exception e1) {
			//Launchdriver.driver.switchTo().frame(0);
			//Launchdriver.driver.findElement(By.linkText("Logout")).click();
			common_util_script.Switchwindow.windowhandleclose();
			System.out.println("Logout successfull 2");
			//Launchdriver.driver.quit();
		}
	}
	
	@DataProvider(name = "DP")
 	 public static String[][] excelRead() throws Exception {		
 		
 		//read the excel file for invalid credentials
 		return Read_file.excelRead("demo_data2.xlsx","C2STransfer");
 	}	

}
