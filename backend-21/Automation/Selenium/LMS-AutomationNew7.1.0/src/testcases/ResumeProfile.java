package testcases;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;
public class ResumeProfile extends ExtentReportMultipleClasses{

	private static String errorfolder = "suspend\\Failure\\";
	
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void resume(String scenario,String des,String profile, String version) throws Exception {
			// TODO Auto-generated method stub
			
		test = extent.createTest(des);
		
		System.out.println("clicking on Loyalty Management");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Loyalty Management"));
    	
		System.out.println("clicking on Loyalty profile Management");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Loyalty profile management"));
		
		
		System.out.println("clicking on Resume Promotion");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Resume Promotion"));
	
		if(scenario.equalsIgnoreCase("positive")){
			
		System.out.println("Select the LOYALITY PROFILE and VERSION");
		Assert.assertTrue(common_features.ProfileandVersion.selectprofileversion(profile, version));
		
		
		System.out.println("now clicking on resume profile to view detail");
		Assert.assertTrue(common_util_script.ClickButton.click("viewresumedetail"));
		

		System.out.println("now clicking on resume profile");
		Assert.assertTrue(common_util_script.ClickButton.click("resumeprofile"));

		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul"));
		System.out.println("Actual Success message is : " + actualsuccessmessage.getText() );
		
		String expectedsuccessmessage ="Profile "+profile+" is successfully Resumed"; 
	
		System.out.println("Expected Success message is : " + expectedsuccessmessage );
		
		
		common_util_script.Get_screenshot.success("resume", profile + "resume");
		}else{
			System.out.println("Select the LOYALITY PROFILE and VERSION");
			Assert.assertFalse(common_features.ProfileandVersion.selectprofileversion(profile, version));
			
		}
				

		
    }
    

	@AfterClass
	public void teardown() throws Exception
	{
		Login.logoutAndcloseDriver();
	  	
	}
	
	
	
  	@DataProvider(name = "DP")
  	 public static String[][] excelRead() throws Exception {		
  		
  		//read the excel file for invalid credentials
  		return Read_file.excelRead("demo_data.xlsx","resume");
  	}	

}
