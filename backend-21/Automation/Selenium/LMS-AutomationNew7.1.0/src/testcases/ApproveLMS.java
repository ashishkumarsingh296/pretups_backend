package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class ApproveLMS extends ExtentReportMultipleClasses{

	private static String approvesuccess = "Approve_LMS\\";

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void approvelmsprofile (String des,String profile, String version) throws Exception {       
        
		test = extent.createTest("To verify that Network admin is able to approve profile "+profile+" with version "+version+"");
		
        //clicking on LMS
    	common_features.LMSOptions.clicklms("Loyalty Management");
    	
        common_features.LMSOptions.clicklms("Approve Loyalty Profile");
        
        System.out.println("Select the LOYALITY PROFILE and VERSION");
		common_features.ProfileandVersion.selectprofileversion(profile, version);
        
		System.out.println("Now clicking on the SUBMIT to approve the LMS");
		Launchdriver.driver.findElement(By.xpath("//*[@name='viewapprovaldetail']")).click();
		
		System.out.println("Now clicking on the APPROVE to approve the LMS");
		Launchdriver.driver.findElement(By.xpath("//*[@name='approveprofile']")).click();
		
		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/ul/li"));
		System.out.println("Actual Success message is : " + actualsuccessmessage.getText() );
		
		String expectedsuccessmessage = "Profile " +  profile + " is successfully Approved"; 
		//String expectedsuccessmessage = "Profile " +  profile + " is successfully Approved, Lms profile cache is required to load the changes for processing.";
		System.out.println("Expected Success message is : " + expectedsuccessmessage );
		
		System.out.println("Now comparing the success message");
		Assert.assertEquals(actualsuccessmessage.getText(), expectedsuccessmessage, "Actual message is not as per the expected message. Test case is failed");
		
		System.out.println("Actual message is same as that of the expected message. Now saving the screenshot of the success approval");
		common_util_script.Get_screenshot.success(approvesuccess, profile + "_isapproved");
		
		System.out.println("Your testcase is passed");
		
		
       	}
	
	@AfterClass
	public void teardown() throws Exception
	{
		Login.logoutAndcloseDriver();
	  	
	}

	
  	@DataProvider(name = "DP")
  	 public static String[][] excelRead() throws Exception {
  		
  		//read the excel file for invalid credentials
  		return Read_file.excelRead("demo_data.xlsx","ApproveProfile");
  	}	

}
