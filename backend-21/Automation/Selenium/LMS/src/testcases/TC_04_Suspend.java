package testcases;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class TC_04_Suspend extends ExtentReportMultipleClasses{
	
	private static String errorfolder = "suspend\\Failure\\";
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}


	@Test(dataProvider = "DP")
	public static void suspend(String profile, String version) throws Exception {
			// TODO Auto-generated method stub
			
		test = extent.createTest("To verify that user is able to Suspend LMS profile "+profile+" with version "+ version, "User should be able to login with valid credentials");
		System.out.println("clicking on Loyalty Management");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Loyalty Management"));
    	
		System.out.println("clicking on Loyalty profile Management");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Loyalty profile management"));
        
        System.out.println("Select the LOYALITY PROFILE and VERSION");
        Assert.assertTrue(common_features.ProfileandVersion.selectprofileversion(profile, version));
        
        
		
        System.out.println("clicking on suspend button ");
		Assert.assertTrue(common_util_script.ClickButton.click("suspendProfile"));

		
		System.out.println("alert is accepted");
		common_features.LMSOptions.alert();
		
		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul"));
		System.out.println("Actual Success message is : " + actualsuccessmessage.getText() );
		
		String expectedsuccessmessage = "Successfully suspended"; 
		System.out.println("Expected Success message is : " + expectedsuccessmessage );
		
		
		common_util_script.Get_screenshot.success("suspend", profile + "suspend");
		}

	@AfterClass
	public void teardown() throws Exception
	{
		Login.logoutAndcloseDriver();
	  	
	}
	
  	@DataProvider(name = "DP")
  	 public static String[][] excelRead() throws Exception {		
  		
  		//read the excel file for invalid credentials
  		return Read_file.excelRead("BaseFile.xlsx","Suspendprofile");
  	}	


	}

