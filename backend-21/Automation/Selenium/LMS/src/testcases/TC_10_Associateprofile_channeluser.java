package testcases;

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

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Launchdriver;
import common_util_script.Read_file;

public class TC_10_Associateprofile_channeluser extends ExtentReportMultipleClasses{


	private static String Scenario = "associate_profile";
	private static String errorfolder = "Associate_Profile\\Failure\\";
	private static String approvesuccess = "Associate_Profile\\Success\\";
	
	
	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsChanneladmin();
	}

	
	@Test(dataProvider = "DP")
	public static void Associateprofile (String loginid, String Profilename, String controlgroup) throws Exception {
		// TODO Auto-generated method stub
		
		test = extent.createTest("To verify that user is able to Associate LMS Profile "+Profilename+" with user "+loginid, "User should be able to login with valid credentials");
		
		//clicking on LMS
		System.out.println("Now clicking on channel user option");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Channel user"),"Channel user link does not exists");
		System.out.println("Channel user is clicked successfully");
		
		System.out.println("Now clicking on Associate profile option");
		Assert.assertTrue(common_features.LMSOptions.clicklms("Associate profile"),"Associate profile link is not available");
		System.out.println("Associate porfile is clicked successfully");
		
		//Select sub option
		System.out.println("Entering the Login ID");
		WebElement loginIDD = Launchdriver.driver.findElement(By.xpath("//*[@name='searchLoginId']"));
		Assert.assertTrue(loginIDD.isDisplayed(), "optinout field does not exists");
		loginIDD.sendKeys(loginid);
		System.out.println("You have entered the LoginID: " + loginid);
		
		System.out.println("Now clicking on the SUBMIT button");
		WebElement submit = Launchdriver.driver.findElement(By.xpath("//*[@name='submitAssociate']"));
		Assert.assertTrue(submit.isDisplayed(), "optinout field does not exists");
		submit.click();
		System.out.println("SUBMIT button is clicked successfully");
	
		WebElement lms = Launchdriver.driver.findElement(By.name("lmsProfileId"));
		Assert.assertTrue(lms.isDisplayed(), "Lms option does not exists");
		Select LMS1 = new Select(Launchdriver.driver.findElement(By.name("lmsProfileId")));
		LMS1.selectByVisibleText(Profilename);
		
		System.out.println("You have selected the MLS Profile : " + Profilename );	
		
		switch (controlgroup) {
		
		case "Yes":
			WebElement enableoptinout = Launchdriver.driver.findElement(By.xpath("//*[@name='controlGroup']"));
			Assert.assertTrue(enableoptinout.isDisplayed(), "optinout field does not exists");
			enableoptinout.click();
			break;
		case "No" :
			break;
			}		
		
		Thread.sleep(1000);
		Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='saveAssociate']")).isDisplayed(), "Your test case is failed");
		Assert.assertTrue(common_util_script.ClickButton.click("saveAssociate"), "You have entered an invalid profile");
		Thread.sleep(1000);
				
		System.out.println("Now clicking on CONFIRM button" );	
		Assert.assertTrue(Launchdriver.driver.findElement(By.xpath("//*[@name='confirmAssociate']")).isDisplayed());
		Launchdriver.driver.findElement(By.xpath("//*[@name='confirmAssociate']")).click();
		System.out.println("CONFIRM button is clicked successfully" );
		
		Thread.sleep(500);
		WebElement actualsuccessmessage = Launchdriver.driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul"));
		System.out.println("Actual Success message is : " + actualsuccessmessage.getText() );
		
		String expectedsuccessmessage = "User " +  " successfully updated"; 
		System.out.println("Expected Success message is : " + expectedsuccessmessage );
		
		System.out.println("Now comparing the success message");
		Assert.assertTrue(actualsuccessmessage.getText().contains("successfully updated"), "Actual message is not as per the expected message. Test case is failed");
		
		System.out.println("Actual message is same as that of the expected message. Now saving the screenshot of the success approval");
		common_util_script.Get_screenshot.success(approvesuccess, loginid + "_isapproved");
		
		System.out.println("Your testcase is passed");
		
	}
	
	@AfterClass
	public void teardown() throws Exception {
		//Login.logoutAndcloseDriver();

	}
	
  	@DataProvider(name = "DP")
  	 public static String[][] excelRead() throws Exception {		
  		
  		//read the excel file for invalid credentials
  		return Read_file.excelRead("Basefile2.xlsx","AssociateProfile");
  	}	

	
	
}
