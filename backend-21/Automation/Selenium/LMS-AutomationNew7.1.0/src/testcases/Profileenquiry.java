package testcases;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class Profileenquiry extends ExtentReportMultipleClasses{
	
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsCCE();

	}


	@Test(dataProvider = "DP")
	public static void viewprofile(String Scenario,String des,String profile, String version)
			throws Exception {

		test = extent.createTest(des);	
		System.out.println("");
		System.out.println("TEST SCENARIO : To view the already added LMS profile");

		common_features.LMSOptions.clicklms("LMS Enquiries");

		common_features.LMSOptions.clicklms("Profile Enquiry");

		System.out.println("Select the LOYALITY PROFILE and VERSION");
		
		if(Scenario.equalsIgnoreCase("Positive")){
			Assert.assertTrue(common_features.ProfileandVersion.selectprofileversion(profile, version));
			Launchdriver.driver.findElement(By.name("view")).click();
			String value=Launchdriver.driver.findElement(By.xpath("//tr/td[contains(text(),'"+profile+"')]")).getText();
			System.out.println(value);
		}
		else{
			Assert.assertFalse(common_features.ProfileandVersion.selectprofileversion(profile, version));
			Launchdriver.driver.findElement(By.name("view")).click();			
			
		}
		
		
		//common_util_script.Get_screenshot.success("view", profile + "view");

		

	}

	
	@AfterClass
	public void teardown() throws Exception
	{
		Login.logoutAndcloseDriver();
	  	
	}


	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "ProfileEnquiry ");
	}

}
