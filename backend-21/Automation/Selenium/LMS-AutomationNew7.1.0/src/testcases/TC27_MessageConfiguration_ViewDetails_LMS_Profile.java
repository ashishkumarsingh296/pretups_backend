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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class TC27_MessageConfiguration_ViewDetails_LMS_Profile {

	private static String errorfolder = "Add_LMS\\Failure\\";
	private static String approvesuccess = "Approve_LMS\\";
	
	@BeforeClass
	public static void beforetest() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("");
		System.out.println("TEST SCENARIO : To add the LMS profile by adding" );	
		
		
		//login to the  GUI using the valid credentials
		Login.loginAsNetworkadmin();
	}
	
	@Test(dataProvider = "DP")
	public static void add_approve_lms(String profilename) throws Exception {
		// TODO Auto-generated method stub
		
				// Create FileInputStream Object  to read Webelement values
			 	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
			 	// Create Properties object    to read Webelement values
			 	Properties prop1 = new Properties();  
			 	//load properties file    to read Webelement values
			 	prop1.load(fileInput1);
			 	
			 	Date date = new Date() ;
				SimpleDateFormat dateFormat = new SimpleDateFormat("ddHHmmss") ;
			 			 	
				//clicking on LMS
				common_features.LMSOptions.clicklms("Loyalty Management");
				
				//Select sub option
				common_features.LMSOptions.clicklms("Message Configuration");
				Thread.sleep(2000);
				
				//selecting profile name
				Select obj = new Select(Launchdriver.driver.findElement(By.xpath("//*[@name='selectedProfile']")));
				obj.selectByVisibleText(profilename);
				//common_util_script.Selectfromdropdown.select("//*[@name='selectedProfile']", "Trans1");
				Thread.sleep(2000);
				
				//click on view detail
				common_util_script.ClickButton.click("viewButton");
				Thread.sleep(2000);
				
				
				
				
			
			
				
				System.out.println("Your testcase is passed");
		
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
		Launchdriver.driver.close();
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
  		return Read_file.excelRead("demo_data.xlsx","MessageConfig");
  	}	

}
