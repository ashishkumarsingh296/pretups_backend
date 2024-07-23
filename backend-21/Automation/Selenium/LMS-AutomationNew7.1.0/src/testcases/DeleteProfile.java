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

import common_util_script.Launchdriver;
import common_util_script.Read_file;
public class DeleteProfile {// extends ExtentReportMultipleClasses{

	private static String errorfolder = "suspend\\Failure\\";

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test(dataProvider = "DP")
	public static void delete(String Scenario,String des,String profile, String version)
			throws Exception {
		// TODO Auto-generated method stub

		// test =
		// extent.createTest("To verify that user is able to Delete LMS profile "+profile+" with version "+
		// version, "User should be able to login with valid credentials");
		System.out.println("clicking on Loyalty Management");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Loyalty Management"));

		System.out.println("clicking on Loyalty profile Management");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Loyalty profile management"));

		System.out.println("Select the LOYALITY PROFILE and VERSION");
		

		if (Scenario.equalsIgnoreCase("Positive")) {
			Assert.assertTrue(common_features.ProfileandVersion
					.selectprofileversion(profile, version));
			System.out
					.println("now clicking on delete profile to delete detail");
			Assert.assertTrue(common_util_script.ClickButton
					.click("deleteProfile"));

			System.out.println("alert is accepted");
			common_features.LMSOptions.alert();

			WebElement actualsuccessmessage = Launchdriver.driver
					.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul"));
			System.out.println("Actual Success message is : "
					+ actualsuccessmessage.getText());

			String expectedsuccessmessage = "Successfully deleted, Lms profile cache is required to load the changes for processing.";
			//String expectedsuccessmessage="Successfully deleted";
			System.out.println("Expected Success message is : "
					+ expectedsuccessmessage);

			Assert.assertEquals(actualsuccessmessage.getText(),
					expectedsuccessmessage,
					"Actual message is not as per the expected message. Test case is failed");

			common_util_script.Get_screenshot.success("deleted", profile
					+ "deleted" + "");

		} else {
			boolean isavilable;
			isavilable=common_features.ProfileandVersion.selectprofileversion(profile, version);
			
			if(isavilable){
				
				Assert.assertTrue(common_util_script.ClickButton.click("deleteProfile"));

				System.out.println("alert is accepted");
				common_features.LMSOptions.alert();

				WebElement actualsuccessmessage = Launchdriver.driver
						.findElement(By.xpath("//table/tbody/tr[2]/td[2]/ul/li"));
				System.out.println("Actual Success message is : "+ actualsuccessmessage.getText());

				String expectedsuccessmessage = "Successfully deleted, Lms profile cache is required to load the changes for processing.";

				System.out.println("Expected Success message is : "
						+ expectedsuccessmessage);

				Assert.assertTrue(!actualsuccessmessage.getText().contains(expectedsuccessmessage));

				common_util_script.Get_screenshot.success("deleted", profile
						+ "deleted" + "");
				
			}else{
				System.out.println("profile is not avilale");
			}
		}

	}

	@AfterClass
	public void teardown() throws Exception {
		 Login.logoutAndcloseDriver();

	}

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		return Read_file.excelRead("demo_data.xlsx", "deleteprofile");
	}

}
