package testcases;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;

public class ViewLMSitems extends ExtentReportMultipleClasses{

	@BeforeClass
	public void beforeClass() throws Exception {
		Login.loginAsNetworkadmin();

	}

	@Test
	public static void ViewItems() throws Exception {
		// TODO Auto-generated method stub
		test = extent.createTest("To verify that user is able to View item ", "User should be able to login with valid credentials");
		System.out.println("clicking on Loyalty Administration");
		Assert.assertTrue(common_features.LMSOptions
				.clicklms("Loyalty Administration"));

		System.out.println("clicking on View Items");
		Assert.assertTrue(common_features.LMSOptions.clicklms("View Items"));

	}

	@AfterClass
	public void teardown() throws Exception {
		Login.logoutAndcloseDriver();

	}

}
